package com.naujokaitis.maistas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@DiscriminatorValue("CLIENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends User {

    @Setter
    @NonNull
    private String defaultAddress;

    @Setter
    private int loyaltyPoints;

    @Getter(AccessLevel.NONE)
    @Transient
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @Setter
    @Transient
    private LoyaltyAccount loyaltyAccount;

    public Client(UUID id,
            String username,
            String passwordHash,
            String email,
            String phone,
            String defaultAddress,
            int loyaltyPoints,
            List<PaymentMethod> paymentMethods) {
        super(id, username, passwordHash, email, phone, UserStatus.ACTIVE, UserRole.CLIENT);
        this.defaultAddress = Objects.requireNonNull(defaultAddress, "defaultAddress must not be null");
        this.loyaltyPoints = loyaltyPoints;
        this.paymentMethods = new ArrayList<>(Objects.requireNonNullElse(paymentMethods, List.of()));
    }

    public List<PaymentMethod> getPaymentMethods() {
        return Collections.unmodifiableList(paymentMethods);
    }

    public void addPaymentMethod(PaymentMethod method) {
        paymentMethods.add(Objects.requireNonNull(method, "method must not be null"));
    }

    public void removePaymentMethod(PaymentMethod method) {
        paymentMethods.remove(method);
    }
}
