package com.hotelfacade.hotel.model;

import com.hotelfacade.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A reservation and its life cycle. It protects its own state transitions:
 * services can only be added before check-out, check-in only once, and so on.
 */
public class Reservation {

    private final String id;
    private final String ownerEmail;
    private final Guest guest;
    private final String roomNumber;
    private final RoomType roomType;
    private final StayPeriod period;
    private final int guestCount;
    private final PriceQuote roomCharge;
    private final Instant createdAt;
    private final List<AddedService> services = new ArrayList<>();

    private ReservationStatus status = ReservationStatus.CONFIRMED;
    private String digitalKey;
    private Instant checkedInAt;
    private Instant checkedOutAt;
    private Invoice invoice;

    public Reservation(String id, String ownerEmail, Guest guest, String roomNumber, RoomType roomType,
                       StayPeriod period, int guestCount, PriceQuote roomCharge) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.guest = guest;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.period = period;
        this.guestCount = guestCount;
        this.roomCharge = roomCharge;
        this.createdAt = Instant.now();
    }

    public synchronized void addService(AddedService service) {
        if (status == ReservationStatus.CHECKED_OUT) {
            throw BusinessException.conflict("No se pueden agregar servicios a una reserva finalizada.");
        }
        services.add(service);
    }

    public synchronized void checkIn(String key) {
        if (status != ReservationStatus.CONFIRMED) {
            throw BusinessException.conflict("El check-in ya fue realizado para esta reserva.");
        }
        this.digitalKey = key;
        this.checkedInAt = Instant.now();
        this.status = ReservationStatus.CHECKED_IN;
    }

    /** Throws a friendly error if the reservation is not in the CHECKED_IN state. */
    public synchronized void ensureCanCheckOut() {
        if (status != ReservationStatus.CHECKED_IN) {
            throw BusinessException.conflict(status == ReservationStatus.CHECKED_OUT
                    ? "El check-out ya fue realizado para esta reserva."
                    : "Debes realizar el check-in antes del check-out.");
        }
    }

    public synchronized void checkOut(Invoice finalInvoice) {
        ensureCanCheckOut();
        this.invoice = finalInvoice;
        this.checkedOutAt = Instant.now();
        this.status = ReservationStatus.CHECKED_OUT;
    }

    public synchronized BigDecimal getServicesTotal() {
        return services.stream().map(AddedService::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getCurrentTotal() {
        return roomCharge.total().add(getServicesTotal());
    }

    public boolean isOwnedBy(String email) {
        return ownerEmail.equals(email);
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public Guest getGuest() {
        return guest;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public StayPeriod getPeriod() {
        return period;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public PriceQuote getRoomCharge() {
        return roomCharge;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized List<AddedService> getServices() {
        return Collections.unmodifiableList(new ArrayList<>(services));
    }

    public synchronized ReservationStatus getStatus() {
        return status;
    }

    public synchronized String getDigitalKey() {
        return digitalKey;
    }

    public synchronized Instant getCheckedInAt() {
        return checkedInAt;
    }

    public synchronized Instant getCheckedOutAt() {
        return checkedOutAt;
    }

    public synchronized Invoice getInvoice() {
        return invoice;
    }
}
