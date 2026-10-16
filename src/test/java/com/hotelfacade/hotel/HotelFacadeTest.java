package com.hotelfacade.hotel;

import com.hotelfacade.common.exception.BusinessException;
import com.hotelfacade.hotel.dto.GuestRequest;
import com.hotelfacade.hotel.dto.ReservationRequest;
import com.hotelfacade.hotel.dto.ReservationResponse;
import com.hotelfacade.hotel.facade.HotelFacade;
import com.hotelfacade.hotel.model.ExtraServiceType;
import com.hotelfacade.hotel.model.ReservationStatus;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.repository.ReservationRepository;
import com.hotelfacade.hotel.repository.RoomRepository;
import com.hotelfacade.hotel.subsystem.AccessService;
import com.hotelfacade.hotel.subsystem.AdditionalServicesService;
import com.hotelfacade.hotel.subsystem.BillingService;
import com.hotelfacade.hotel.subsystem.RateService;
import com.hotelfacade.hotel.subsystem.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HotelFacadeTest {

    private static final String OWNER = "ana@correo.com";
    private static final LocalDate TODAY = LocalDate.of(2027, 3, 1);

    private HotelFacade facade;
    private AccessService accessService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        accessService = new AccessService();
        facade = new HotelFacade(new RoomService(new RoomRepository(), clock), new RateService(),
                new AdditionalServicesService(), new BillingService(), accessService,
                new ReservationRepository(), clock);
    }

    @Test
    void hotelHasFifteenRooms() {
        assertThat(facade.checkAvailability(TODAY, TODAY.plusDays(1), null)).hasSize(15);
    }

    @Test
    void fullReservationFlowGeneratesTheFinalInvoice() {
        ReservationResponse created = facade.createReservation(request("201", 2, 3), OWNER);
        assertThat(created.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(created.roomCharge().total()).isEqualByComparingTo(new BigDecimal("360"));

        facade.addService(created.id(), ExtraServiceType.SPA, OWNER);
        facade.addService(created.id(), ExtraServiceType.BREAKFAST, OWNER);

        ReservationResponse checkedIn = facade.checkIn(created.id(), OWNER);
        assertThat(checkedIn.digitalKey()).isNotBlank();
        assertThat(accessService.opensRoom(checkedIn.digitalKey(), "201")).isTrue();

        ReservationResponse checkedOut = facade.checkOut(created.id(), OWNER);
        assertThat(checkedOut.status()).isEqualTo(ReservationStatus.CHECKED_OUT);
        // 3 nights x 120 + spa 50 + breakfast 15 x 3 days
        assertThat(checkedOut.invoice().total()).isEqualByComparingTo(new BigDecimal("455"));
        assertThat(checkedOut.digitalKey()).isNull();
        assertThat(accessService.opensRoom(checkedIn.digitalKey(), "201")).isFalse();
    }

    @Test
    void roomCannotBeBookedTwiceForOverlappingDates() {
        facade.createReservation(request("101", 1, 3), OWNER);

        assertThatThrownBy(() -> facade.createReservation(request("101", 1, 1), OWNER))
                .isInstanceOf(BusinessException.class);
        assertThat(facade.checkAvailability(TODAY, TODAY.plusDays(3), RoomType.SINGLE)).hasSize(5);
    }

    @Test
    void checkOutFreesTheRoom() {
        ReservationResponse created = facade.createReservation(request("301", 2, 2), OWNER);
        facade.checkIn(created.id(), OWNER);
        facade.checkOut(created.id(), OWNER);

        assertThat(facade.checkAvailability(TODAY, TODAY.plusDays(2), RoomType.SUITE)).hasSize(3);
    }

    @Test
    void checkOutRequiresCheckIn() {
        ReservationResponse created = facade.createReservation(request("102", 1, 2), OWNER);

        assertThatThrownBy(() -> facade.checkOut(created.id(), OWNER)).isInstanceOf(BusinessException.class);
    }

    @Test
    void breakfastCanOnlyBeAddedOnce() {
        ReservationResponse created = facade.createReservation(request("103", 1, 2), OWNER);
        facade.addService(created.id(), ExtraServiceType.BREAKFAST, OWNER);

        assertThatThrownBy(() -> facade.addService(created.id(), ExtraServiceType.BREAKFAST, OWNER))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void guestsCannotExceedRoomCapacity() {
        assertThatThrownBy(() -> facade.createReservation(request("104", 2, 2), OWNER))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void otherUsersCannotSeeTheReservation() {
        ReservationResponse created = facade.createReservation(request("105", 1, 2), OWNER);

        assertThatThrownBy(() -> facade.getReservation(created.id(), "otro@correo.com"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void pastDatesAreRejected() {
        assertThatThrownBy(() -> facade.checkAvailability(TODAY.minusDays(1), TODAY.plusDays(1), null))
                .isInstanceOf(BusinessException.class);
    }

    private static ReservationRequest request(String room, int guests, int nights) {
        GuestRequest guest = new GuestRequest("Ana Gomez", "1020304050", "ana@correo.com", "3001234567");
        return new ReservationRequest(room, TODAY, TODAY.plusDays(nights), guests, guest);
    }
}
