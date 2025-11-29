package com.naujokaitis.maistas.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class DeliveryStatus {

    private OrderStatus status;
    private GeoCoordinate location;
    private LocalDateTime timestamp;

    public DeliveryStatus(OrderStatus status,
                          GeoCoordinate location,
                          LocalDateTime timestamp) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.location = location;
        this.timestamp = Objects.requireNonNullElse(timestamp, LocalDateTime.now());
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public GeoCoordinate getLocation() {
        return location;
    }

    public void setLocation(GeoCoordinate location) {
        this.location = location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = Objects.requireNonNullElse(timestamp, LocalDateTime.now());
    }
}

