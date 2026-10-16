package com.hotelfacade.hotel;

import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.RoomType;
import com.hotelfacade.hotel.model.StayPeriod;
import com.hotelfacade.hotel.subsystem.RateService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RateServiceTest {

    private final RateService rateService = new RateService();

    @Test
    void lowSeasonUsesTheBasePrice() {
        StayPeriod period = new StayPeriod(LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 13));

        PriceQuote quote = rateService.quote(RoomType.DOUBLE, period);

        assertThat(quote.nights()).isEqualTo(3);
        assertThat(quote.highSeasonNights()).isZero();
        assertThat(quote.total()).isEqualByComparingTo(new BigDecimal("360"));
    }

    @Test
    void highSeasonMultipliesTheBasePriceByOnePointFive() {
        StayPeriod period = new StayPeriod(LocalDate.of(2027, 7, 1), LocalDate.of(2027, 7, 3));

        PriceQuote quote = rateService.quote(RoomType.SINGLE, period);

        assertThat(quote.highSeasonPrice()).isEqualByComparingTo(new BigDecimal("120"));
        assertThat(quote.total()).isEqualByComparingTo(new BigDecimal("240"));
    }

    @Test
    void staysCrossingSeasonsArePricedNightByNight() {
        // Nights of May 30 and 31 are low season; June 1 is high season.
        StayPeriod period = new StayPeriod(LocalDate.of(2027, 5, 30), LocalDate.of(2027, 6, 2));

        PriceQuote quote = rateService.quote(RoomType.SUITE, period);

        assertThat(quote.lowSeasonNights()).isEqualTo(2);
        assertThat(quote.highSeasonNights()).isEqualTo(1);
        assertThat(quote.total()).isEqualByComparingTo(new BigDecimal("875"));
    }
}
