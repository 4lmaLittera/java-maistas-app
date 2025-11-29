package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class OrderItem {

    private final MenuItem menuItem;
    private int quantity;
    private BigDecimal unitPrice;
    private String specialInstructions;

    public OrderItem(MenuItem menuItem,
            int quantity,
            BigDecimal unitPrice,
            String specialInstructions) {
        this.menuItem = Objects.requireNonNull(menuItem, "menuItem must not be null");
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        this.specialInstructions = specialInstructions;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
