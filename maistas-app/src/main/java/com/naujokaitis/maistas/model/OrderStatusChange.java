package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class OrderStatusChange {

    private final OrderStatus status;
    private final LocalDateTime changedAt;
    private final User changedBy;
    private final String note;

    public OrderStatusChange(OrderStatus status,
                             LocalDateTime changedAt,
                             User changedBy,
                             String note) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.changedAt = Objects.requireNonNullElse(changedAt, LocalDateTime.now());
        this.changedBy = Objects.requireNonNull(changedBy, "changedBy must not be null");
        this.note = note;
    }

}

