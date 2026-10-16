package com.hotelfacade.hotel.model;

import java.math.BigDecimal;

public record InvoiceLine(String description, int quantity, BigDecimal unitPrice, BigDecimal total) {
}
