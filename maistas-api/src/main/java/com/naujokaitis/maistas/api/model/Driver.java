package com.naujokaitis.maistas.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicleType")
    private VehicleType vehicleType;

    @Column(name = "available")
    private boolean available;

    public Driver(UUID id, String username, String password, String email, String phone,
                  VehicleType vehicleType, boolean available) {
        super(id, username, password, email, phone, UserStatus.ACTIVE, UserRole.DRIVER);
        this.vehicleType = vehicleType;
        this.available = available;
    }

    // Factory method for creating new drivers
    public static Driver create(String username, String password, String email, String phone,
                                VehicleType vehicleType, boolean available) {
        return new Driver(UUID.randomUUID(), username, password, email, phone,
                vehicleType, available);
    }
}
