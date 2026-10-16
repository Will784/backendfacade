package com.hotelfacade.hotel.model;

/**
 * Personal data of the main guest of a reservation.
 */
public record Guest(String fullName, String documentNumber, String email, String phone) {
}
