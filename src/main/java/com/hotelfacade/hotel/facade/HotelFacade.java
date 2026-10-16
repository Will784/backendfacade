package com.hotelfacade.hotel.facade;

import com.hotelfacade.common.exception.BusinessException;
import com.hotelfacade.hotel.dto.AvailableRoomResponse;
import com.hotelfacade.hotel.dto.GuestRequest;
import com.hotelfacade.hotel.dto.ReservationRequest;
import com.hotelfacade.hotel.dto.ReservationResponse;
import com.hotelfacade.hotel.model.ExtraServiceType;
import com.hotelfacade.hotel.model.Guest;
import com.hotelfacade.hotel.model.Invoice;
import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.Reservation;
import com.hotelfacade.hotel.model.Room;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.model.StayPeriod;
import com.hotelfacade.hotel.repository.ReservationRepository;
import com.hotelfacade.hotel.subsystem.AccessService;
import com.hotelfacade.hotel.subsystem.AdditionalServicesService;
import com.hotelfacade.hotel.subsystem.BillingService;
import com.hotelfacade.hotel.subsystem.RateService;
import com.hotelfacade.hotel.subsystem.RoomService;
import com.hotelfacade.hotel.subsystem.ServiceCatalogItem;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * FACADE PATTERN: single, simple entry point to the hotel.
 *
 * <p>The controller (client) only talks to this class. Each method coordinates the
 * subsystems in the right order, hiding how rooms, rates, services, billing and
 * access keys work internally.</p>
 *
 * <p>Mapping to the requirement names: crearReserva = {@link #createReservation},
 * agregarServicio = {@link #addService}, realizarCheckIn = {@link #checkIn},
 * realizarCheckOut = {@link #checkOut}.</p>
 */
@Component
public class HotelFacade {

    private final RoomService roomService;
    private final RateService rateService;
    private final AdditionalServicesService additionalServicesService;
    private final BillingService billingService;
    private final AccessService accessService;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public HotelFacade(RoomService roomService, RateService rateService,
                       AdditionalServicesService additionalServicesService, BillingService billingService,
                       AccessService accessService, ReservationRepository reservationRepository, Clock clock) {
        this.roomService = roomService;
        this.rateService = rateService;
        this.additionalServicesService = additionalServicesService;
        this.billingService = billingService;
        this.accessService = accessService;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    /** Free rooms for the dates (optionally of one type), each one with its price preview. */
    public List<AvailableRoomResponse> checkAvailability(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        StayPeriod period = roomService.createStayPeriod(checkIn, checkOut);
        return roomService.findAvailableRooms(period, type).stream()
                .map(room -> AvailableRoomResponse.from(room, rateService.quote(room.getType(), period)))
                .toList();
    }

    /** crearReserva: validates dates, books the room and stores the reservation with its price. */
    public ReservationResponse createReservation(ReservationRequest request, String ownerEmail) {
        StayPeriod period = roomService.createStayPeriod(request.checkIn(), request.checkOut());
        String reservationId = reservationRepository.nextId();

        Room room = roomService.reserveRoom(request.roomNumber(), reservationId, period, request.guestCount());
        PriceQuote roomCharge = rateService.quote(room.getType(), period);

        Reservation reservation = new Reservation(reservationId, ownerEmail, toGuest(request.guest()),
                room.getNumber(), room.getType(), period, request.guestCount(), roomCharge);
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    /** agregarServicio: adds an extra service with its cost to the reservation. */
    public ReservationResponse addService(String reservationId, ExtraServiceType serviceType, String ownerEmail) {
        Reservation reservation = findOwnedReservation(reservationId, ownerEmail);
        additionalServicesService.addService(reservation, serviceType);
        return ReservationResponse.from(reservation);
    }

    /** realizarCheckIn: generates the digital key of the room and marks the guest as checked in. */
    public ReservationResponse checkIn(String reservationId, String ownerEmail) {
        Reservation reservation = findOwnedReservation(reservationId, ownerEmail);

        if (LocalDate.now(clock).isAfter(reservation.getPeriod().checkOut())) {
            throw BusinessException.conflict("La fecha de salida de esta reserva ya pasó; no es posible hacer check-in.");
        }

        synchronized (reservation) {
            String key = accessService.generateDigitalKey(reservation.getRoomNumber());
            try {
                reservation.checkIn(key);
            } catch (BusinessException ex) {
                accessService.revokeKey(key);
                throw ex;
            }
        }
        return ReservationResponse.from(reservation);
    }

    /**
     * realizarCheckOut: calculates the additional charges, generates the final invoice,
     * revokes the digital key and frees the room.
     */
    public ReservationResponse checkOut(String reservationId, String ownerEmail) {
        Reservation reservation = findOwnedReservation(reservationId, ownerEmail);

        synchronized (reservation) {
            reservation.ensureCanCheckOut();
            Invoice invoice = billingService.generateInvoice(reservation);
            reservation.checkOut(invoice);
            accessService.revokeKey(reservation.getDigitalKey());
            roomService.releaseRoom(reservation.getRoomNumber(), reservation.getId());
        }
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse getReservation(String reservationId, String ownerEmail) {
        return ReservationResponse.from(findOwnedReservation(reservationId, ownerEmail));
    }

    public List<ReservationResponse> getReservationsOf(String ownerEmail) {
        return reservationRepository.findByOwner(ownerEmail).stream().map(ReservationResponse::from).toList();
    }

    public List<ServiceCatalogItem> getServiceCatalog() {
        return additionalServicesService.getCatalog();
    }

    /**
     * A user can only see and change their own reservations.
     * Someone else's reservation answers "not found" so its existence is not revealed.
     */
    private Reservation findOwnedReservation(String reservationId, String ownerEmail) {
        return reservationRepository.findById(reservationId.trim().toUpperCase(Locale.ROOT))
                .filter(reservation -> reservation.isOwnedBy(ownerEmail))
                .orElseThrow(() -> BusinessException.notFound("No se encontró la reserva solicitada."));
    }

    private static Guest toGuest(GuestRequest guest) {
        return new Guest(guest.fullName().trim(), guest.documentNumber(),
                guest.email().trim().toLowerCase(Locale.ROOT), guest.phone());
    }
}
