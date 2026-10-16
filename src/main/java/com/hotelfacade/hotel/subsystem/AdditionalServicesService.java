package com.hotelfacade.hotel.subsystem;

import com.hotelfacade.common.exception.BusinessException;
import com.hotelfacade.hotel.model.AddedService;
import com.hotelfacade.hotel.model.ChargeMode;
import com.hotelfacade.hotel.model.ExtraServiceType;
import com.hotelfacade.hotel.model.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Subsystem 3 (ServicioAdicionalService): adds extra services with their cost
 * (spa $50, breakfast $15 per day, transfer $30).
 */
@Service
public class AdditionalServicesService {

    public static final int MAX_SERVICES_PER_RESERVATION = 10;

    public List<ServiceCatalogItem> getCatalog() {
        return Arrays.stream(ExtraServiceType.values()).map(ServiceCatalogItem::from).toList();
    }

    public AddedService addService(Reservation reservation, ExtraServiceType type) {
        List<AddedService> current = reservation.getServices();

        if (current.size() >= MAX_SERVICES_PER_RESERVATION) {
            throw BusinessException.badRequest("Una reserva puede tener máximo "
                    + MAX_SERVICES_PER_RESERVATION + " servicios adicionales.");
        }
        if (!type.isRepeatable() && current.stream().anyMatch(service -> service.type() == type)) {
            throw BusinessException.conflict("El servicio " + type.getDisplayName()
                    + " ya está incluido en esta reserva.");
        }

        int quantity = type.getChargeMode() == ChargeMode.PER_NIGHT ? reservation.getPeriod().nights() : 1;
        BigDecimal total = type.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
        AddedService service = new AddedService(type, type.getDisplayName(), type.getUnitPrice(),
                quantity, total, Instant.now());

        reservation.addService(service);
        return service;
    }
}
