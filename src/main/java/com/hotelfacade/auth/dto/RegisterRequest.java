package com.hotelfacade.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres.")
        @Pattern(regexp = ValidationRules.PERSON_NAME, message = "El nombre solo puede contener letras y espacios.")
        String fullName,

        @NotBlank(message = "El correo es obligatorio.")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
        @Email(message = "El correo no tiene un formato válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres.")
        @Pattern(regexp = ValidationRules.STRONG_PASSWORD,
                message = "La contraseña debe tener mayúscula, minúscula y número, sin espacios.")
        String password
) {
}
