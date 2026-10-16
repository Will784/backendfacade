package com.hotelfacade.hotel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Body of POST /api/hotel/reservar (the "reservaDTO" of the requirements).
 */
public record ReservationRequest(
        @NotBlank(message = "Debes seleccionar una habitación.")
        @Pattern(regexp = "^\\d{3}$", message = "El número de habitación no es válido.")
        String roomNumber,

        @NotNull(message = "La fecha de entrada es obligatoria.")
        LocalDate checkIn,

        @NotNull(message = "La fecha de salida es obligatoria.")
        LocalDate checkOut,

        @NotNull(message = "El número de huéspedes es obligatorio.")
        @Min(value = 1, message = "Debe haber al menos 1 huésped.")
        @Max(value = 4, message = "El máximo es de 4 huéspedes por habitación.")
        Integer guestCount,

        @NotNull(message = "Los datos del huésped son obligatorios.")
        @Valid
        GuestRequest guest
) {
}
