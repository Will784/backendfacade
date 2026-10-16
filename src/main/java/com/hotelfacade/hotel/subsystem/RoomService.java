package com.hotelfacade.hotel.subsystem;

import com.hotelfacade.common.exception.BusinessException;
import com.hotelfacade.hotel.model.Room;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.model.StayPeriod;
import com.hotelfacade.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Subsystem 1 (HabitacionService): checks availability by dates and type, books and releases rooms.
 */
@Service
public class RoomService {

    public static final int MAX_NIGHTS = 30;
    public static final int MAX_DAYS_IN_ADVANCE = 365;

    private final RoomRepository roomRepository;
    private final Clock clock;

    public RoomService(RoomRepository roomRepository, Clock clock) {
        this.roomRepository = roomRepository;
        this.clock = clock;
    }

    /**
     * Builds a stay period after checking the hotel rules:
     * no past dates, at most one year in advance and at most 30 nights.
     */
    public StayPeriod createStayPeriod(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now(clock);

        if (checkIn.isBefore(today)) {
            throw BusinessException.badRequest("La fecha de entrada no puede ser anterior a hoy.");
        }
        if (checkIn.isAfter(today.plusDays(MAX_DAYS_IN_ADVANCE))) {
            throw BusinessException.badRequest("Solo se puede reservar con máximo un año de anticipación.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw BusinessException.badRequest("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        StayPeriod period = new StayPeriod(checkIn, checkOut);
        if (period.nights() > MAX_NIGHTS) {
            throw BusinessException.badRequest("La estadía no puede superar " + MAX_NIGHTS + " noches.");
        }
        return period;
    }

    /**
     * @param type room type to filter by, or null to include every type
     */
    public List<Room> findAvailableRooms(StayPeriod period, RoomType type) {
        return roomRepository.findAll().stream()
                .filter(room -> type == null || room.getType() == type)
                .filter(room -> room.isAvailableFor(period))
                .toList();
    }

    public Room getRoom(String roomNumber) {
        return roomRepository.findByNumber(roomNumber)
                .orElseThrow(() -> BusinessException.notFound("La habitación " + roomNumber + " no existe."));
    }

    public Room reserveRoom(String roomNumber, String reservationId, StayPeriod period, int guestCount) {
        Room room = getRoom(roomNumber);

        if (guestCount > room.getType().getMaxGuests()) {
            throw BusinessException.badRequest("La habitación " + room.getType().getDisplayName()
                    + " admite máximo " + room.getType().getMaxGuests() + " huésped(es).");
        }
        if (!room.book(reservationId, period)) {
            throw BusinessException.conflict("La habitación " + roomNumber
                    + " ya no está disponible para esas fechas.");
        }
        return room;
    }

    public void releaseRoom(String roomNumber, String reservationId) {
        getRoom(roomNumber).release(reservationId);
    }
}
