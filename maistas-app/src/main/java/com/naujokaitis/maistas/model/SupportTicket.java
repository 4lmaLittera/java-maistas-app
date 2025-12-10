package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "support_tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportTicket {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private Administrator assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @Setter
    @NonNull
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    public SupportTicket(UUID id,
                         Order order,
                         User createdBy,
                         String description) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.order = Objects.requireNonNull(order, "order must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.status = TicketStatus.OPEN;
    }

    public void assignTo(Administrator administrator) {
        this.assignedTo = administrator;
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void resolve(String resolution) {
        this.resolution = Objects.requireNonNull(resolution, "resolution must not be null");
        this.status = TicketStatus.RESOLVED;
    }

    public void close() {
        this.status = TicketStatus.CLOSED;
    }
}

