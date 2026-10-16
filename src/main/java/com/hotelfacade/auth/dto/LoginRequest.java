package com.hotelfacade.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio.")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
        @Email(message = "El correo no tiene un formato válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(max = 64, message = "La contraseña no puede superar 64 caracteres.")
        String password
) {
}
