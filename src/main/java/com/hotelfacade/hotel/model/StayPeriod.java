package com.hotelfacade.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Value object for a stay: from the check-in date (inclusive) to the check-out date (exclusive).
 * Example: 10 to 12 means the nights of the 10th and the 11th.
 */
public record StayPeriod(LocalDate checkIn, LocalDate checkOut) {

    public StayPeriod {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("The check-out date must be after the check-in date");
        }
    }

    public int nights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /** Dates of every night of the stay. */
    public List<LocalDate> nightDates() {
        return checkIn.datesUntil(checkOut).toList();
    }

    public boolean overlaps(StayPeriod other) {
        return checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(checkOut);
    }
}
