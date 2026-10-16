package com.hotelfacade.hotel.model;

/**
 * How an additional service is charged.
 */
public enum ChargeMode {
    /** A single charge each time the service is added. */
    ONE_TIME,
    /** The price is multiplied by the number of nights (days) of the stay. */
    PER_NIGHT
}
