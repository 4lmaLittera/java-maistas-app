package com.naujokaitis.maistas.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class SupportTicket {

    private final UUID id;
    private final Order order;
    private final User createdBy;
    private Administrator assignedTo;
    private TicketStatus status;
    @Setter
    @NonNull
    private String description;
    private String resolution;

    public SupportTicket(UUID id,
                         Order order,
                         User createdBy,
                         String description) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.order = order;
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

