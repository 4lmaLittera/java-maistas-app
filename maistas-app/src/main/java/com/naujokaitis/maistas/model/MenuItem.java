package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public class MenuItem {

    private final UUID id;
    @Setter
    @NonNull
    private String name;
    @Setter
    private String description;
    @Setter
    @NonNull
    private BigDecimal price;
    @Setter
    @NonNull
    private MenuCategory category;
    @Setter
    private int inventoryCount;

    public MenuItem(UUID id,
            String name,
            String description,
            BigDecimal price,
            MenuCategory category,
            int inventoryCount) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = description;
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.inventoryCount = inventoryCount;
    }

    public BigDecimal calculatePrice(java.time.LocalDateTime time, DemandLevel demandLevel) {
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(demandLevel, "demandLevel must not be null");

        double modifier = switch (demandLevel) {
            case LOW -> 0.9;
            case MEDIUM -> 1.0;
            case HIGH -> 1.15;
            case PEAK -> 1.3;
        };
        return price.multiply(BigDecimal.valueOf(modifier));
    }
}
