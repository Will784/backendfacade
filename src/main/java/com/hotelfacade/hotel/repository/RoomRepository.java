package com.hotelfacade.hotel.repository;

import com.hotelfacade.hotel.model.Room;
import com.hotelfacade.hotel.model.RoomType;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory catalog with the 15 predefined rooms of the hotel.
 * Floor 1: singles, floor 2: doubles, floor 3: doubles and suites.
 */
@Repository
public class RoomRepository {

    private final Map<String, Room> roomsByNumber = new LinkedHashMap<>();

    public RoomRepository() {
        addRooms(1, RoomType.SINGLE, 101, 106);
        addRooms(2, RoomType.DOUBLE, 201, 206);
        addRooms(3, RoomType.SUITE, 301, 303);
    }

    public List<Room> findAll() {
        return Collections.unmodifiableList(List.copyOf(roomsByNumber.values()));
    }

    public Optional<Room> findByNumber(String number) {
        return Optional.ofNullable(roomsByNumber.get(number));
    }

    private void addRooms(int floor, RoomType type, int firstNumber, int lastNumber) {
        for (int number = firstNumber; number <= lastNumber; number++) {
            String roomNumber = String.valueOf(number);
            roomsByNumber.put(roomNumber, new Room(roomNumber, type, floor));
        }
    }
}
