package com.hotelfacade.hotel.subsystem;

import com.hotelfacade.hotel.model.ChargeMode;
import com.hotelfacade.hotel.model.ExtraServiceType;

import java.math.BigDecimal;

/**
 * Additional service as shown in the catalog.
 */
public record ServiceCatalogItem(ExtraServiceType type, String name, BigDecimal unitPrice,
                                 ChargeMode chargeMode, boolean repeatable) {

    public static ServiceCatalogItem from(ExtraServiceType type) {
        return new ServiceCatalogItem(type, type.getDisplayName(), type.getUnitPrice(),
                type.getChargeMode(), type.isRepeatable());
    }
}
