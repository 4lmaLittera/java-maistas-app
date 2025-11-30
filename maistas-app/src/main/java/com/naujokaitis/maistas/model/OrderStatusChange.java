package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChange {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "changed_by_id")
    private UUID changedBy;

    @Column(name = "note")
    private String note;

}
