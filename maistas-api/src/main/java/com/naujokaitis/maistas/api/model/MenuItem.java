package com.naujokaitis.maistas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MenuCategory category;

    @Column(name = "inventory_count")
    private int inventoryCount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    public MenuItem(UUID id, String name, String description, BigDecimal price, MenuCategory category, int inventoryCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.inventoryCount = inventoryCount;
    }

    public static MenuItem create(String name, String description, BigDecimal price, MenuCategory category, int inventoryCount) {
        return new MenuItem(UUID.randomUUID(), name, description, price, category, inventoryCount);
    }
}
