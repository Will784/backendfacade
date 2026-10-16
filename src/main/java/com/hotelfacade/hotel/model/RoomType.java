package com.hotelfacade.hotel.model;

import java.math.BigDecimal;

/**
 * Room categories with their base price per night (low season) and maximum number of guests.
 */
public enum RoomType {

    SINGLE("Sencilla", new BigDecimal("80"), 1),
    DOUBLE("Doble", new BigDecimal("120"), 2),
    SUITE("Suite", new BigDecimal("250"), 4);

    private final String displayName;
    private final BigDecimal basePrice;
    private final int maxGuests;

    RoomType(String displayName, BigDecimal basePrice, int maxGuests) {
        this.displayName = displayName;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public int getMaxGuests() {
        return maxGuests;
    }
}
