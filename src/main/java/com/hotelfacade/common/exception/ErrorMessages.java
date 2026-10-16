package com.hotelfacade.common.exception;

/**
 * User-facing messages (in Spanish, because they are shown in the interface).
 * Keeping them in one place makes them easy to review and change.
 */
public final class ErrorMessages {

    public static final String INVALID_FORM = "Revisa los datos del formulario.";
    public static final String INVALID_PARAMETERS = "Uno o más datos de la consulta no son válidos.";
    public static final String INVALID_FORMAT = "El formato de los datos enviados no es válido.";
    public static final String MISSING_PARAMETER = "Falta un dato obligatorio en la consulta.";
    public static final String RESOURCE_NOT_FOUND = "El recurso solicitado no existe.";
    public static final String METHOD_NOT_ALLOWED = "Operación no permitida.";
    public static final String UNEXPECTED = "Ocurrió un error inesperado. Intenta nuevamente en unos minutos.";
    public static final String UNAUTHORIZED = "Tu sesión no es válida o expiró. Inicia sesión nuevamente.";
    public static final String FORBIDDEN = "No tienes permiso para realizar esta acción.";

    private ErrorMessages() {
    }
}
