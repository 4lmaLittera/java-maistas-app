package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
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
    @Column(name = "default_address", nullable = true)
    private String defaultAddress;

    @Setter
    private int loyaltyPoints;

    @Getter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER) // Eager fetch simpler for this UI
    @CollectionTable(name = "client_payment_methods", joinColumns = @JoinColumn(name = "client_id"))
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
            List<PaymentMethod> paymentMethods,
            BigDecimal walletBalance) {
        super(id, username, passwordHash, email, phone, UserStatus.ACTIVE, UserRole.CLIENT);
        this.defaultAddress = Objects.requireNonNull(defaultAddress, "defaultAddress must not be null");
        this.loyaltyPoints = loyaltyPoints;
        this.paymentMethods = new ArrayList<>(Objects.requireNonNullElse(paymentMethods, List.of()));
        this.walletBalance = Objects.requireNonNullElse(walletBalance, BigDecimal.ZERO);
    }

    public List<PaymentMethod> getPaymentMethods() {
        return Collections.unmodifiableList(paymentMethods);
    }

    public void addPaymentMethod(PaymentMethod method) {
        paymentMethods.add(Objects.requireNonNull(method, "method must not be null"));
    }

    @Setter
    @Column(name = "wallet_balance", nullable = true, precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    public void addToWallet(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.walletBalance = this.walletBalance.add(amount);
        }
    }

    public void removePaymentMethod(PaymentMethod method) {
        paymentMethods.remove(method);
    }

    public void updatePaymentMethods(List<PaymentMethod> newMethods) {
        this.paymentMethods.clear();
        if (newMethods != null) {
            this.paymentMethods.addAll(newMethods);
        }
    }
}
