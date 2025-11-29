package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PaymentMethod {

    private PaymentType paymentType;
    private String details;
    private boolean isDefault;

    public PaymentMethod(PaymentType paymentType, String details, boolean isDefault) {
        this.paymentType = Objects.requireNonNull(paymentType, "paymentType must not be null");
        this.details = Objects.requireNonNull(details, "details must not be null");
        this.isDefault = isDefault;
    }
}
