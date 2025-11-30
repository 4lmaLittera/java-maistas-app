package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Setter
    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @NonNull
    @Column(name = "address", nullable = false)
    private String address;

    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Setter
    @Column(name = "rating")
    private Double rating;

    @Setter
    @Embedded
    private OperatingSchedule operatingHours;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private RestaurantOwner owner;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private List<PricingRule> pricingRules = new ArrayList<>();

    public Restaurant(UUID id,
            String name,
            String address) {
        this(id, name, address, null, null, null, null, null, List.of());
    }

    public Restaurant(UUID id,
            String name,
            String address,
            String description,
            Double rating,
            OperatingSchedule operatingHours,
            Menu menu,
            RestaurantOwner owner,
            List<PricingRule> pricingRules) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.address = Objects.requireNonNull(address, "address must not be null");
        this.description = description;
        this.rating = rating;
        this.operatingHours = operatingHours;
        this.menu = menu;
        this.owner = owner;
        this.pricingRules = new ArrayList<>(Objects.requireNonNullElse(pricingRules, List.of()));
    }

    public List<PricingRule> getPricingRules() {
        return Collections.unmodifiableList(pricingRules);
    }

    public void setOwner(RestaurantOwner owner) {
        this.owner = owner;
    }

    public RestaurantOwner getOwner() {
        return owner;
    }

    public void addPricingRule(PricingRule pricingRule) {
        pricingRules.add(Objects.requireNonNull(pricingRule, "pricingRule must not be null"));
    }

    public void removePricingRule(PricingRule pricingRule) {
        pricingRules.remove(pricingRule);
    }
}
