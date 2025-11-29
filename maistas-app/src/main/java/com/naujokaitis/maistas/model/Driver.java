package com.naujokaitis.maistas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Driver extends User {

    @Setter
    @NonNull
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Setter
    private boolean available;

    @Transient
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
