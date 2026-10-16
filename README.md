# Sistema de Reservas de Hotel — Backend (Patrón Facade)

API REST en **Java 21 + Spring Boot 3** para el taller de Patrones de Software.
El código está en inglés; los mensajes que ve el usuario están en español.

## Patrón Facade

```
HotelController (cliente)
        │
        ▼
   HotelFacade  ──►  RoomService                (HabitacionService)
                ──►  RateService                (TarifaService)
                ──►  AdditionalServicesService  (ServicioAdicionalService)
                ──►  BillingService             (FacturacionService)
                ──►  AccessService              (AccesoService)
```

- El **controlador** solo habla con `HotelFacade`; no conoce los subsistemas.
- Los **subsistemas** no se conocen entre sí; cada uno hace una sola tarea.
- `HotelFacade` coordina el orden de las operaciones:

| Requerimiento        | Método en `HotelFacade` | Qué coordina |
|----------------------|-------------------------|--------------|
| `crearReserva`       | `createReservation`     | valida fechas → bloquea habitación → calcula tarifa → guarda reserva |
| `agregarServicio`    | `addService`            | agrega servicio y su costo |
| `realizarCheckIn`    | `checkIn`               | genera llave digital (UUID) |
| `realizarCheckOut`   | `checkOut`              | factura final → revoca llave → libera habitación |

## Reglas de negocio

| Regla | Valor |
|-------|-------|
| Habitaciones | 15: 6 sencillas (101–106), 6 dobles (201–206), 3 suites (301–303) |
| Precio base por noche | Sencilla $80 · Doble $120 · Suite $250 |
| Capacidad | Sencilla 1 · Doble 2 · Suite 4 huéspedes |
| Temporada alta (precio x1.5) | junio, julio, diciembre y enero; cada noche se cobra según su temporada |
| Servicios | Spa $50 (por uso) · Desayuno $15 por día · Traslado $30 (por uso) |
| Límites | máx. 30 noches, máx. 1 año de anticipación, máx. 10 servicios, desayuno solo 1 vez |
| Estados | `CONFIRMED` → `CHECKED_IN` → `CHECKED_OUT` |

El estado se guarda **en memoria** con colecciones Java (`ConcurrentHashMap`), por lo que se reinicia
cuando el servidor se reinicia.

## Endpoints

Públicos:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro (`fullName`, `email`, `password`) → token |
| POST | `/api/auth/login` | Inicio de sesión (`email`, `password`) → token |
| GET  | `/api/health` | Estado del servicio |

Protegidos (header `Authorization: Bearer <token>`):

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET  | `/api/hotel/disponibilidad?checkIn=AAAA-MM-DD&checkOut=AAAA-MM-DD&type=SINGLE` | Habitaciones libres con precio (`type` opcional: `SINGLE`, `DOUBLE`, `SUITE`) |
| POST | `/api/hotel/reservar` | Crea la reserva |
| POST | `/api/hotel/servicios/{reservaId}` | Agrega servicio (`{"serviceType":"SPA"}`) |
| PUT  | `/api/hotel/checkin/{reservaId}` | Check-in y llave digital |
| PUT  | `/api/hotel/checkout/{reservaId}` | Check-out y factura final |
| GET  | `/api/hotel/reserva/{reservaId}` | Detalle de la reserva |
| GET  | `/api/hotel/reservas` | Reservas del usuario (extra) |
| GET  | `/api/hotel/catalogo-servicios` | Catálogo de servicios (extra) |

Ejemplo de `POST /api/hotel/reservar`:

```json
{
  "roomNumber": "201",
  "checkIn": "2026-11-20",
  "checkOut": "2026-11-23",
  "guestCount": 2,
  "guest": {
    "fullName": "Ana Gómez",
    "documentNumber": "1020304050",
    "email": "ana@correo.com",
    "phone": "3001234567"
  }
}
```

## Seguridad

- Contraseñas guardadas con **BCrypt**; login con **JWT** (expira en 120 min por defecto).
- Bloqueo temporal (5 min) tras 5 intentos fallidos de login.
- Cada usuario solo puede ver y modificar **sus** reservas.
- Los errores devuelven solo `{ status, message, fieldErrors? }`: nunca stack traces ni nombres de clases.
  Los detalles técnicos quedan solo en el log del servidor.

## Estructura

```
src/main/java/com/hotelfacade
├── auth/            registro, login, usuarios
├── security/        JWT, filtro, bloqueo por intentos
├── config/          seguridad, CORS, reloj
├── common/          manejo de errores y health check
└── hotel/
    ├── controller/  HotelController (cliente de la fachada)
    ├── facade/      HotelFacade
    ├── subsystem/   los 5 subsistemas
    ├── model/       entidades y objetos de valor
    ├── repository/  almacenamiento en memoria
    └── dto/         datos de entrada y salida
```

## Ejecutar en local

Requisitos: Java 21 y Maven 3.9+.

```bash
mvn spring-boot:run        # http://localhost:8080
mvn test                   # pruebas automáticas
```

## Variables de entorno

| Variable | Descripción | Por defecto |
|----------|-------------|-------------|
| `PORT` | Puerto (Render lo define solo) | `8080` |
| `JWT_SECRET` | Clave para firmar los tokens | aleatoria en cada arranque |
| `JWT_EXPIRATION_MINUTES` | Duración del token | `120` |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend separados por coma (acepta `https://*.vercel.app`) | `http://localhost:5173` |
| `APP_TIMEZONE` | Zona horaria del hotel | `America/Bogota` |

## Despliegue en Render

**Opción A: Blueprint (recomendada)**

1. Sube este repositorio a GitHub.
2. En [Render](https://dashboard.render.com) → **New** → **Blueprint** → elige el repositorio.
   Render lee `render.yaml`, crea el servicio Docker y genera `JWT_SECRET` automáticamente.
3. Cuando lo pida, escribe `CORS_ALLOWED_ORIGINS` con la URL del frontend en Vercel, por ejemplo
   `https://hotel-facade.vercel.app,https://*.vercel.app`.
4. Espera a que termine el build y abre `https://<tu-servicio>.onrender.com/api/health` → `{"status":"UP"}`.

**Opción B: manual**

1. **New** → **Web Service** → conecta el repositorio.
2. Runtime: **Docker** (usa el `Dockerfile` de la raíz). Plan: Free.
3. Health Check Path: `/api/health`.
4. Agrega las variables `JWT_SECRET` (texto largo aleatorio) y `CORS_ALLOWED_ORIGINS`.

> En el plan gratuito el servicio se duerme tras 15 min sin uso; la primera petición puede tardar
> ~1 minuto y los datos en memoria se pierden al reiniciarse.
