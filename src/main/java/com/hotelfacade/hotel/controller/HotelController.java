package com.hotelfacade.hotel.controller;

import com.hotelfacade.hotel.dto.AddServiceRequest;
import com.hotelfacade.hotel.dto.AvailableRoomResponse;
import com.hotelfacade.hotel.dto.ReservationRequest;
import com.hotelfacade.hotel.dto.ReservationResponse;
import com.hotelfacade.hotel.facade.HotelFacade;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.subsystem.ServiceCatalogItem;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST client of the facade. It does not contain business logic: it only receives
 * the request, delegates to {@link HotelFacade} and returns the result.
 * The paths are the ones required by the assignment.
 */
@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelFacade hotelFacade;

    public HotelController(HotelFacade hotelFacade) {
        this.hotelFacade = hotelFacade;
    }

    @GetMapping("/disponibilidad")
    public List<AvailableRoomResponse> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType type) {
        return hotelFacade.checkAvailability(checkIn, checkOut, type);
    }

    @PostMapping("/reservar")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request,
                                                 @AuthenticationPrincipal String userEmail) {
        return hotelFacade.createReservation(request, userEmail);
    }

    @PostMapping("/servicios/{reservationId}")
    public ReservationResponse addService(@PathVariable String reservationId,
                                          @Valid @RequestBody AddServiceRequest request,
                                          @AuthenticationPrincipal String userEmail) {
        return hotelFacade.addService(reservationId, request.serviceType(), userEmail);
    }

    @PutMapping("/checkin/{reservationId}")
    public ReservationResponse checkIn(@PathVariable String reservationId,
                                       @AuthenticationPrincipal String userEmail) {
        return hotelFacade.checkIn(reservationId, userEmail);
    }

    @PutMapping("/checkout/{reservationId}")
    public ReservationResponse checkOut(@PathVariable String reservationId,
                                        @AuthenticationPrincipal String userEmail) {
        return hotelFacade.checkOut(reservationId, userEmail);
    }

    @GetMapping("/reserva/{reservationId}")
    public ReservationResponse getReservation(@PathVariable String reservationId,
                                              @AuthenticationPrincipal String userEmail) {
        return hotelFacade.getReservation(reservationId, userEmail);
    }

    /** Extra endpoint: reservations of the logged-in user (used by the management panel). */
    @GetMapping("/reservas")
    public List<ReservationResponse> getMyReservations(@AuthenticationPrincipal String userEmail) {
        return hotelFacade.getReservationsOf(userEmail);
    }

    /** Extra endpoint: catalog of additional services with their prices. */
    @GetMapping("/catalogo-servicios")
    public List<ServiceCatalogItem> getServiceCatalog() {
        return hotelFacade.getServiceCatalog();
    }
}
