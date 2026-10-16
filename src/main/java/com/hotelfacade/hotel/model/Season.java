package com.hotelfacade.hotel.model;

import java.math.BigDecimal;

/**
 * Hotel seasons. High season multiplies the base price by 1.5.
 */
public enum Season {

    LOW(BigDecimal.ONE),
    HIGH(new BigDecimal("1.5"));

    private final BigDecimal priceMultiplier;

    Season(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }
}
