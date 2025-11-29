package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Restaurant {

    private final UUID id;

    @Setter
    @NonNull
    private String name;

    @Setter
    @NonNull
    private String address;

    @Setter
    private String description;

    @Setter
    private Double rating;

    @Setter
    private OperatingSchedule operatingHours;

    @Setter
    private Menu menu;

    @Getter(AccessLevel.NONE)
    private final List<PricingRule> pricingRules;

    public Restaurant(UUID id,
                      String name,
                      String address) {
        this(id, name, address, null, null, null, null, List.of());
    }

    public Restaurant(UUID id,
                      String name,
                      String address,
                      String description,
                      Double rating,
                      OperatingSchedule operatingHours,
                      Menu menu,
                      List<PricingRule> pricingRules) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.address = Objects.requireNonNull(address, "address must not be null");
        this.description = description;
        this.rating = rating;
        this.operatingHours = operatingHours;
        this.menu = menu;
        this.pricingRules = new ArrayList<>(Objects.requireNonNullElse(pricingRules, List.of()));
    }

    public List<PricingRule> getPricingRules() {
        return Collections.unmodifiableList(pricingRules);
    }

    public void addPricingRule(PricingRule pricingRule) {
        pricingRules.add(Objects.requireNonNull(pricingRule, "pricingRule must not be null"));
    }

    public void removePricingRule(PricingRule pricingRule) {
        pricingRules.remove(pricingRule);
    }
}

