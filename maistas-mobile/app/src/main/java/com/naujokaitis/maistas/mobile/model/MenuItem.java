package com.naujokaitis.maistas.mobile.model;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuItem {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int inventoryCount;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getInventoryCount() { return inventoryCount; }
    public void setInventoryCount(int inventoryCount) { this.inventoryCount = inventoryCount; }
}
