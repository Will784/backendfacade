package com.hotelfacade.hotel.subsystem;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subsystem 5 (AccesoService): generates simulated digital keys (UUID) for the booked rooms.
 */
@Service
public class AccessService {

    /** Active key -> room number. */
    private final Map<String, String> activeKeys = new ConcurrentHashMap<>();

    public String generateDigitalKey(String roomNumber) {
        String key = UUID.randomUUID().toString();
        activeKeys.put(key, roomNumber);
        return key;
    }

    public boolean opensRoom(String key, String roomNumber) {
        return key != null && roomNumber.equals(activeKeys.get(key));
    }

    public void revokeKey(String key) {
        if (key != null) {
            activeKeys.remove(key);
        }
    }
}
