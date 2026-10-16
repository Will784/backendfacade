package com.hotelfacade.hotel.model;

import java.math.BigDecimal;

/**
 * Price of the room for a stay, split by season.
 */
public record PriceQuote(RoomType roomType, int nights,
                         int lowSeasonNights, BigDecimal lowSeasonPrice,
                         int highSeasonNights, BigDecimal highSeasonPrice,
                         BigDecimal total) {
}
