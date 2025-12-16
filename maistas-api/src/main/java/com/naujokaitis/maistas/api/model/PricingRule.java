package com.naujokaitis.maistas.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PricingRule {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private TimeRange timeRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "demand_level", nullable = false)
    private DemandLevel demandLevel;

    @Column(name = "price_modifier", nullable = false)
    private double priceModifier;

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

    public static PricingRule create(String name, TimeRange timeRange, DemandLevel demandLevel, double priceModifier) {
        return new PricingRule(UUID.randomUUID(), name, timeRange, demandLevel, priceModifier);
    }
}
