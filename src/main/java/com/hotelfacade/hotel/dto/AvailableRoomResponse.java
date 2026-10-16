package com.hotelfacade.hotel.dto;

import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.Room;
import com.hotelfacade.hotel.model.RoomType;

/**
 * A free room for the searched dates, including the price preview for that stay.
 */
public record AvailableRoomResponse(String number, RoomType type, String typeName, int floor,
                                    int maxGuests, PriceQuote quote) {

    public static AvailableRoomResponse from(Room room, PriceQuote quote) {
        RoomType type = room.getType();
        return new AvailableRoomResponse(room.getNumber(), type, type.getDisplayName(), room.getFloor(),
                type.getMaxGuests(), quote);
    }
}
