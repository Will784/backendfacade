package com.hotelfacade.common.web;

import com.hotelfacade.common.exception.ApiError;
import com.hotelfacade.common.exception.ErrorMessages;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Replaces Spring's default "/error" page so that errors raised outside the controllers
 * (for example inside filters) also return a clean JSON body without internal details.
 */
@RestController
public class FallbackErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiError> handleError(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        String message = switch (status) {
            case NOT_FOUND -> ErrorMessages.RESOURCE_NOT_FOUND;
            case UNAUTHORIZED -> ErrorMessages.UNAUTHORIZED;
            case FORBIDDEN -> ErrorMessages.FORBIDDEN;
            case BAD_REQUEST -> ErrorMessages.INVALID_FORMAT;
            default -> ErrorMessages.UNEXPECTED;
        };
        return ResponseEntity.status(status).body(ApiError.of(status.value(), message));
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (code instanceof Integer value) {
            HttpStatus status = HttpStatus.resolve(value);
            if (status != null) {
                return status;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
