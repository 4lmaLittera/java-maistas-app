package com.naujokaitis.maistas.mobile.model;

import java.util.List;
import java.util.UUID;

public class Menu {
    private UUID id;
    private String name;
    private List<MenuItem> items;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<MenuItem> getItems() { return items; }
    public void setItems(List<MenuItem> items) { this.items = items; }
}
