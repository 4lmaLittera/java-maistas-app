package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public class PricingRule {

    private final UUID id;
    private final String name;
    private final TimeRange timeRange;
    private final DemandLevel demandLevel;
    private final double priceModifier;

    public PricingRule(UUID id,
                       String name,
                       TimeRange timeRange,
                       DemandLevel demandLevel,
                       double priceModifier) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.timeRange = Objects.requireNonNull(timeRange, "timeRange must not be null");
        this.demandLevel = Objects.requireNonNull(demandLevel, "demandLevel must not be null");
        this.priceModifier = priceModifier;
    }

    public BigDecimal apply(MenuItem item, BigDecimal basePrice) {
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(basePrice, "basePrice must not be null");
        return basePrice.multiply(BigDecimal.valueOf(priceModifier));
    }
}

