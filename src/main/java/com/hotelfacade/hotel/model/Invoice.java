package com.hotelfacade.hotel.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Final invoice generated at check-out, with the detailed breakdown of every charge.
 */
public record Invoice(String number, String reservationId, Instant issuedAt, String guestName,
                      List<InvoiceLine> lines, BigDecimal roomSubtotal, BigDecimal servicesSubtotal,
                      BigDecimal total) {
}
