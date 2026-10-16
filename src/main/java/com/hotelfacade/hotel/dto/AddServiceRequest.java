package com.hotelfacade.hotel.dto;

import com.hotelfacade.hotel.model.ExtraServiceType;
import jakarta.validation.constraints.NotNull;

/**
 * Body of POST /api/hotel/servicios/{reservaId}.
 */
public record AddServiceRequest(
        @NotNull(message = "Debes seleccionar un servicio.")
        ExtraServiceType serviceType
) {
}
