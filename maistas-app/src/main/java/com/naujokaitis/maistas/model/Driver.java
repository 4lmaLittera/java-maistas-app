package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Driver extends User {

    @Setter
    @NonNull
    private VehicleType vehicleType;

    @Setter
    private boolean available;

    private Order currentOrder;

    public Driver(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone,
            VehicleType vehicleType,
            boolean available) {
        super(id, username, passwordHash, email, phone, UserStatus.ACTIVE, UserRole.DRIVER);
        this.vehicleType = Objects.requireNonNull(vehicleType, "vehicleType must not be null");
        this.available = available;
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
        this.available = order == null;
    }
}
