# Guía de commits — Backend

Descomprime el zip y copia su contenido dentro de tu repositorio clonado.
Luego ejecuta los bloques **en orden**: cada uno agrega solo los archivos de esa parte,
así el historial queda con commits claros.

## 1. chore: initialize Spring Boot project skeleton

```bash
git add .gitignore \
  pom.xml \
  src/main/java/com/hotelfacade/HotelReservationsApplication.java \
  src/main/resources/application.properties
git commit -m "chore: initialize Spring Boot project skeleton"
```

## 2. feat: add global error handling that hides internal details

Every exception is translated into a friendly JSON message; stack traces
and exception names are only written to the server log.

```bash
git add src/main/java/com/hotelfacade/common/exception/ApiError.java \
  src/main/java/com/hotelfacade/common/exception/BusinessException.java \
  src/main/java/com/hotelfacade/common/exception/ErrorMessages.java \
  src/main/java/com/hotelfacade/common/exception/GlobalExceptionHandler.java \
  src/main/java/com/hotelfacade/common/web/FallbackErrorController.java \
  src/main/java/com/hotelfacade/common/web/HealthController.java
git commit -m "feat: add global error handling that hides internal details"
```

## 3. feat: add user registration and JWT login

- Users are stored in memory with BCrypt-hashed passwords
- Stateless security: every /api/hotel request needs a Bearer token
- Failed logins temporarily lock the account after 5 attempts
- 401/403 responses use the same friendly JSON body
- CORS origins are configurable through CORS_ALLOWED_ORIGINS

```bash
git add src/main/java/com/hotelfacade/auth/AuthController.java \
  src/main/java/com/hotelfacade/auth/AuthService.java \
  src/main/java/com/hotelfacade/auth/dto/AuthResponse.java \
  src/main/java/com/hotelfacade/auth/dto/LoginRequest.java \
  src/main/java/com/hotelfacade/auth/dto/RegisterRequest.java \
  src/main/java/com/hotelfacade/auth/dto/ValidationRules.java \
  src/main/java/com/hotelfacade/auth/model/User.java \
  src/main/java/com/hotelfacade/auth/repository/UserRepository.java \
  src/main/java/com/hotelfacade/config/SecurityConfig.java \
  src/main/java/com/hotelfacade/security/JsonSecurityErrorHandler.java \
  src/main/java/com/hotelfacade/security/JwtAuthenticationFilter.java \
  src/main/java/com/hotelfacade/security/JwtService.java \
  src/main/java/com/hotelfacade/security/LoginAttemptService.java
git commit -m "feat: add user registration and JWT login"
```

## 4. feat: add hotel domain model and in-memory repositories

- Room, Reservation, StayPeriod, Invoice and related value objects
- 15 predefined rooms: 6 singles, 6 doubles and 3 suites
- Reservations are stored in a ConcurrentHashMap

```bash
git add src/main/java/com/hotelfacade/hotel/model/AddedService.java \
  src/main/java/com/hotelfacade/hotel/model/ChargeMode.java \
  src/main/java/com/hotelfacade/hotel/model/ExtraServiceType.java \
  src/main/java/com/hotelfacade/hotel/model/Guest.java \
  src/main/java/com/hotelfacade/hotel/model/Invoice.java \
  src/main/java/com/hotelfacade/hotel/model/InvoiceLine.java \
  src/main/java/com/hotelfacade/hotel/model/PriceQuote.java \
  src/main/java/com/hotelfacade/hotel/model/Reservation.java \
  src/main/java/com/hotelfacade/hotel/model/ReservationStatus.java \
  src/main/java/com/hotelfacade/hotel/model/Room.java \
  src/main/java/com/hotelfacade/hotel/model/RoomType.java \
  src/main/java/com/hotelfacade/hotel/model/Season.java \
  src/main/java/com/hotelfacade/hotel/model/StayPeriod.java \
  src/main/java/com/hotelfacade/hotel/repository/ReservationRepository.java \
  src/main/java/com/hotelfacade/hotel/repository/RoomRepository.java
git commit -m "feat: add hotel domain model and in-memory repositories"
```

## 5. feat: add the five hotel subsystems

- RoomService (HabitacionService): availability, booking and release
- RateService (TarifaService): price per night by season (high = x1.5)
- AdditionalServicesService (ServicioAdicionalService): spa, breakfast, transfer
- BillingService (FacturacionService): detailed invoice
- AccessService (AccesoService): simulated digital keys (UUID)
The subsystems do not know each other; the facade will coordinate them.

```bash
git add src/main/java/com/hotelfacade/config/TimeConfig.java \
  src/main/java/com/hotelfacade/hotel/subsystem/AccessService.java \
  src/main/java/com/hotelfacade/hotel/subsystem/AdditionalServicesService.java \
  src/main/java/com/hotelfacade/hotel/subsystem/BillingService.java \
  src/main/java/com/hotelfacade/hotel/subsystem/RateService.java \
  src/main/java/com/hotelfacade/hotel/subsystem/RoomService.java \
  src/main/java/com/hotelfacade/hotel/subsystem/ServiceCatalogItem.java
git commit -m "feat: add the five hotel subsystems"
```

## 6. feat: add HotelFacade and REST endpoints

HotelFacade is the single entry point that coordinates the five subsystems:
createReservation, addService, checkIn and checkOut (which generates the
final invoice, revokes the digital key and frees the room).
Endpoints: POST /api/hotel/reservar, GET /api/hotel/disponibilidad,
POST /api/hotel/servicios/{id}, PUT /api/hotel/checkin/{id},
PUT /api/hotel/checkout/{id}, GET /api/hotel/reserva/{id},
plus GET /api/hotel/reservas and GET /api/hotel/catalogo-servicios.

```bash
git add src/main/java/com/hotelfacade/hotel/controller/HotelController.java \
  src/main/java/com/hotelfacade/hotel/dto/AddServiceRequest.java \
  src/main/java/com/hotelfacade/hotel/dto/AvailableRoomResponse.java \
  src/main/java/com/hotelfacade/hotel/dto/GuestRequest.java \
  src/main/java/com/hotelfacade/hotel/dto/ReservationRequest.java \
  src/main/java/com/hotelfacade/hotel/dto/ReservationResponse.java \
  src/main/java/com/hotelfacade/hotel/facade/HotelFacade.java
git commit -m "feat: add HotelFacade and REST endpoints"
```

## 7. test: cover rates, facade flow and API security

- Season pricing, including stays that cross seasons
- Full flow: reserve, add services, check-in, check-out and invoice
- Double booking, capacity, ownership and date rules
- Token required, generic login error, no internal details on bad input

```bash
git add src/test/java/com/hotelfacade/auth/ApiSecurityTest.java \
  src/test/java/com/hotelfacade/hotel/HotelFacadeTest.java \
  src/test/java/com/hotelfacade/hotel/RateServiceTest.java
git commit -m "test: cover rates, facade flow and API security"
```

## 8. build: add Docker and Render deployment configuration

- Multi-stage Dockerfile (Maven build + JRE runtime, non-root user)
- render.yaml blueprint with health check and generated JWT secret
- README with architecture, endpoints, business rules and deploy steps

```bash
git add .dockerignore \
  Dockerfile \
  README.md \
  render.yaml
git commit -m "build: add Docker and Render deployment configuration"
```

## Último paso: la propia guía

```bash
git add GUIA_COMMITS.md
git commit -m "docs: add commit guide"
git push -u origin main
```
