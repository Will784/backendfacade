package com.hotelfacade.hotel.model;

import java.math.BigDecimal;

/**
 * Additional services offered by the hotel.
 */
public enum ExtraServiceType {

    SPA("Spa", new BigDecimal("50"), ChargeMode.ONE_TIME, true),
    BREAKFAST("Desayuno", new BigDecimal("15"), ChargeMode.PER_NIGHT, false),
    TRANSFER("Traslado", new BigDecimal("30"), ChargeMode.ONE_TIME, true);

    private final String displayName;
    private final BigDecimal unitPrice;
    private final ChargeMode chargeMode;
    private final boolean repeatable;

    ExtraServiceType(String displayName, BigDecimal unitPrice, ChargeMode chargeMode, boolean repeatable) {
        this.displayName = displayName;
        this.unitPrice = unitPrice;
        this.chargeMode = chargeMode;
        this.repeatable = repeatable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public ChargeMode getChargeMode() {
        return chargeMode;
    }

    /** Whether the service can be added more than once to the same reservation. */
    public boolean isRepeatable() {
        return repeatable;
    }
}
