package com.naujokaitis.maistas.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@DiscriminatorValue("CLIENT")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends User {

    @Column(name = "default_address", nullable = true)
    private String defaultAddress;

    @Column(name = "loyaltyPoints")
    private Integer loyaltyPoints = 0;

    @Column(name = "wallet_balance", nullable = true, precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    public Client(UUID id, String username, String password, String email, String phone,
                  String defaultAddress, int loyaltyPoints, BigDecimal walletBalance) {
        super(id, username, password, email, phone, UserStatus.ACTIVE, UserRole.CLIENT);
        this.defaultAddress = defaultAddress;
        this.loyaltyPoints = loyaltyPoints;
        this.walletBalance = walletBalance != null ? walletBalance : BigDecimal.ZERO;
    }

    // Factory method for creating new clients
    public static Client create(String username, String password, String email, String phone,
                                String defaultAddress, int loyaltyPoints, BigDecimal walletBalance) {
        return new Client(UUID.randomUUID(), username, password, email, phone,
                defaultAddress, loyaltyPoints, walletBalance);
    }
}
