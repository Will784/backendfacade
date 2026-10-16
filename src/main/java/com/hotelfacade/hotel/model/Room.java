package com.hotelfacade.hotel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A hotel room. It keeps the list of periods in which it is booked,
 * so it can answer by itself whether it is free for a given stay.
 */
public class Room {

    private final String number;
    private final RoomType type;
    private final int floor;
    private final List<Booking> bookings = new ArrayList<>();

    public Room(String number, RoomType type, int floor) {
        this.number = number;
        this.type = type;
        this.floor = floor;
    }

    public synchronized boolean isAvailableFor(StayPeriod period) {
        return bookings.stream().noneMatch(booking -> booking.period().overlaps(period));
    }

    /**
     * Blocks the room for the period.
     *
     * @return false if the room was already taken for those dates
     */
    public synchronized boolean book(String reservationId, StayPeriod period) {
        if (!isAvailableFor(period)) {
            return false;
        }
        bookings.add(new Booking(reservationId, period));
        return true;
    }

    public synchronized void release(String reservationId) {
        bookings.removeIf(booking -> booking.reservationId().equals(reservationId));
    }

    public String getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    private record Booking(String reservationId, StayPeriod period) {
    }
}
