package com.naujokaitis.maistas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "menu"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    public OrderItem(UUID id, MenuItem menuItem, int quantity, BigDecimal unitPrice, String specialInstructions) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.specialInstructions = specialInstructions;
    }

    public static OrderItem create(MenuItem menuItem, int quantity, String specialInstructions) {
        return new OrderItem(UUID.randomUUID(), menuItem, quantity, menuItem.getPrice(), specialInstructions);
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
