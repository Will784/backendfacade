package com.hotelfacade.hotel.subsystem;

import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.model.Season;
import com.hotelfacade.hotel.model.StayPeriod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.Set;

/**
 * Subsystem 2 (TarifaService): calculates the room price using the season of each night.
 * High season = base price x 1.5, low season = base price.
 */
@Service
public class RateService {

    /** Vacation months in Colombia: mid-year and end of year. */
    public static final Set<Month> HIGH_SEASON_MONTHS =
            EnumSet.of(Month.JUNE, Month.JULY, Month.DECEMBER, Month.JANUARY);

    public Season seasonOf(LocalDate night) {
        return HIGH_SEASON_MONTHS.contains(night.getMonth()) ? Season.HIGH : Season.LOW;
    }

    public BigDecimal priceFor(RoomType type, Season season) {
        return type.getBasePrice().multiply(season.getPriceMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }

    /** Each night is priced with its own season, so a stay can mix both seasons. */
    public PriceQuote quote(RoomType type, StayPeriod period) {
        int highNights = (int) period.nightDates().stream()
                .filter(night -> seasonOf(night) == Season.HIGH)
                .count();
        int lowNights = period.nights() - highNights;

        BigDecimal lowPrice = priceFor(type, Season.LOW);
        BigDecimal highPrice = priceFor(type, Season.HIGH);
        BigDecimal total = lowPrice.multiply(BigDecimal.valueOf(lowNights))
                .add(highPrice.multiply(BigDecimal.valueOf(highNights)));

        return new PriceQuote(type, period.nights(), lowNights, lowPrice, highNights, highPrice, total);
    }
}
