package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class PaymentMethod {

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "details", nullable = false)
    private String details;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public PaymentMethod(PaymentType paymentType, String details, boolean isDefault) {
        this.paymentType = Objects.requireNonNull(paymentType, "paymentType must not be null");
        this.details = Objects.requireNonNull(details, "details must not be null");
        this.isDefault = isDefault;
    }
    
    @Override
    public String toString() {
        return (isDefault ? "[Default] " : "") + paymentType + ": " + details;
    }
}
