package com.hotelfacade.hotel.dto;

import com.hotelfacade.auth.dto.ValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Main guest data typed in the reservation form.
 */
public record GuestRequest(
        @NotBlank(message = "El nombre del huésped es obligatorio.")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres.")
        @Pattern(regexp = ValidationRules.PERSON_NAME, message = "El nombre solo puede contener letras y espacios.")
        String fullName,

        @NotBlank(message = "El documento es obligatorio.")
        @Pattern(regexp = "^\\d{6,12}$", message = "El documento debe tener entre 6 y 12 dígitos, sin puntos ni espacios.")
        String documentNumber,

        @NotBlank(message = "El correo del huésped es obligatorio.")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
        @Email(message = "El correo no tiene un formato válido.")
        String email,

        @NotBlank(message = "El teléfono es obligatorio.")
        @Pattern(regexp = "^\\d{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos, sin espacios.")
        String phone
) {
}
