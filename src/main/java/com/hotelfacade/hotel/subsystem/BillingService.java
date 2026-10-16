package com.hotelfacade.hotel.subsystem;

import com.hotelfacade.hotel.model.AddedService;
import com.hotelfacade.hotel.model.ChargeMode;
import com.hotelfacade.hotel.model.Invoice;
import com.hotelfacade.hotel.model.InvoiceLine;
import com.hotelfacade.hotel.model.PriceQuote;
import com.hotelfacade.hotel.model.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Subsystem 4 (FacturacionService): generates the detailed invoice of a reservation.
 */
@Service
public class BillingService {

    private final AtomicLong invoiceSequence = new AtomicLong();

    public Invoice generateInvoice(Reservation reservation) {
        List<InvoiceLine> lines = new ArrayList<>(roomLines(reservation));
        reservation.getServices().stream().map(BillingService::serviceLine).forEach(lines::add);

        BigDecimal roomSubtotal = reservation.getRoomCharge().total();
        BigDecimal servicesSubtotal = reservation.getServicesTotal();

        return new Invoice(nextInvoiceNumber(), reservation.getId(), Instant.now(),
                reservation.getGuest().fullName(), List.copyOf(lines),
                roomSubtotal, servicesSubtotal, roomSubtotal.add(servicesSubtotal));
    }

    private List<InvoiceLine> roomLines(Reservation reservation) {
        PriceQuote quote = reservation.getRoomCharge();
        String room = "Habitación " + reservation.getRoomNumber() + " (" + quote.roomType().getDisplayName() + ")";
        List<InvoiceLine> lines = new ArrayList<>();

        if (quote.lowSeasonNights() > 0) {
            lines.add(line(room + " - noches en temporada baja", quote.lowSeasonNights(), quote.lowSeasonPrice()));
        }
        if (quote.highSeasonNights() > 0) {
            lines.add(line(room + " - noches en temporada alta", quote.highSeasonNights(), quote.highSeasonPrice()));
        }
        return lines;
    }

    private static InvoiceLine serviceLine(AddedService service) {
        String suffix = service.type().getChargeMode() == ChargeMode.PER_NIGHT ? " (por día de estadía)" : "";
        return new InvoiceLine("Servicio: " + service.name() + suffix, service.quantity(),
                service.unitPrice(), service.total());
    }

    private static InvoiceLine line(String description, int quantity, BigDecimal unitPrice) {
        return new InvoiceLine(description, quantity, unitPrice, unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    private String nextInvoiceNumber() {
        return String.format("FAC-%06d", invoiceSequence.incrementAndGet());
    }
}
