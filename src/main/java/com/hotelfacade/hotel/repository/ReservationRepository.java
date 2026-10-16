package com.hotelfacade.hotel.repository;

import com.hotelfacade.hotel.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage of reservations, using a Java ConcurrentHashMap.
 */
@Repository
public class ReservationRepository {

    private final Map<String, Reservation> reservationsById = new ConcurrentHashMap<>();

    /** Short, readable identifier such as "R-1A2B3C4D". */
    public String nextId() {
        String id;
        do {
            id = "R-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (reservationsById.containsKey(id));
        return id;
    }

    public void save(Reservation reservation) {
        reservationsById.put(reservation.getId(), reservation);
    }

    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservationsById.get(id));
    }

    /** Reservations of a user, newest first. */
    public List<Reservation> findByOwner(String ownerEmail) {
        return reservationsById.values().stream()
                .filter(reservation -> reservation.isOwnedBy(ownerEmail))
                .sorted(Comparator.comparing(Reservation::getCreatedAt).reversed())
                .toList();
    }
}
