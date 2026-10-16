package com.hotelfacade.hotel.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * An additional service already added to a reservation, with its calculated cost.
 */
public record AddedService(ExtraServiceType type, String name, BigDecimal unitPrice, int quantity,
                           BigDecimal total, Instant addedAt) {
}
