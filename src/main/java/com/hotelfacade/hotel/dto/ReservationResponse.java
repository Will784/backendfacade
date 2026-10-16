package com.hotelfacade.hotel.dto;

import com.hotelfacade.hotel.model.AddedService;
import com.hotelfacade.hotel.model.Guest;
import com.hotelfacade.hotel.model.Invoice;
import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.Reservation;
import com.hotelfacade.hotel.model.ReservationStatus;
import com.hotelfacade.hotel.model.RoomType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Reservation summary sent to the frontend.
 * The digital key is only included while the guest is checked in.
 */
public record ReservationResponse(String id, ReservationStatus status, String roomNumber, RoomType roomType,
                                  String roomTypeName, LocalDate checkIn, LocalDate checkOut, int nights,
                                  int guestCount, Guest guest, PriceQuote roomCharge,
                                  List<AddedService> services, BigDecimal servicesTotal,
                                  BigDecimal currentTotal, String digitalKey, Instant createdAt,
                                  Instant checkedInAt, Instant checkedOutAt, Invoice invoice) {

    public static ReservationResponse from(Reservation reservation) {
        boolean keyActive = reservation.getStatus() == ReservationStatus.CHECKED_IN;
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getRoomNumber(),
                reservation.getRoomType(),
                reservation.getRoomType().getDisplayName(),
                reservation.getPeriod().checkIn(),
                reservation.getPeriod().checkOut(),
                reservation.getPeriod().nights(),
                reservation.getGuestCount(),
                reservation.getGuest(),
                reservation.getRoomCharge(),
                reservation.getServices(),
                reservation.getServicesTotal(),
                reservation.getCurrentTotal(),
                keyActive ? reservation.getDigitalKey() : null,
                reservation.getCreatedAt(),
                reservation.getCheckedInAt(),
                reservation.getCheckedOutAt(),
                reservation.getInvoice());
    }
}
