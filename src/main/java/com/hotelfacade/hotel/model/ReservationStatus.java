package com.hotelfacade.hotel.model;

/**
 * Life cycle of a reservation: CONFIRMED -> CHECKED_IN -> CHECKED_OUT.
 */
public enum ReservationStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT
}
