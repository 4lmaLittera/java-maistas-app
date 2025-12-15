package com.naujokaitis.maistas.mobile.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {
    private String menuItemId;
    private String name;
    private BigDecimal price;
    private int quantity;

    public CartItem(String menuItemId, String name, BigDecimal price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
