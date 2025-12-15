package com.naujokaitis.maistas.mobile.model;

import java.util.UUID;

public class Restaurant {
    private UUID id;
    private String name;
    private String address;
    private String description;
    private Double rating;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
