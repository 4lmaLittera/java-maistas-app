package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Setter
    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Setter
    @NonNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Setter
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MenuCategory category;

    @Setter
    @Column(name = "inventory_count")
    private int inventoryCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    public MenuItem(UUID id,
            String name,
            String description,
            BigDecimal price,
            MenuCategory category,
            int inventoryCount,
            Menu menu) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = description;
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.inventoryCount = inventoryCount;
        this.menu = menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
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
