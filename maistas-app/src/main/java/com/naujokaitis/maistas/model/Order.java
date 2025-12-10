package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_status_history", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderStatusChange> statusHistory = new ArrayList<>();

    @Setter
    @Embedded
    private DeliveryStatus deliveryStatus;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_thread_id")
    private ChatThread chatThread;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "placed_at", nullable = false)
    private LocalDateTime placedAt;

    @Setter
    @NonNull
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private OrderStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    public Order(UUID id,
            Client client,
            Restaurant restaurant,
            String deliveryAddress,
            PaymentType paymentType) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant must not be null");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress must not be null");
        this.paymentType = Objects.requireNonNullElse(paymentType, PaymentType.CARD);
        this.items = new ArrayList<>();
        this.statusHistory = new ArrayList<>();
        this.totalPrice = BigDecimal.ZERO;
        this.placedAt = LocalDateTime.now();
        this.currentStatus = OrderStatus.CREATED;
        recordStatusChange(OrderStatus.CREATED, client.getId(), "Order created");
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(OrderItem item) {
        items.add(Objects.requireNonNull(item, "item must not be null"));
        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        recalculateTotal();
    }

    public List<OrderStatusChange> getStatusHistory() {
        return Collections.unmodifiableList(statusHistory);
    }

    public void updateStatus(OrderStatus newStatus, User actor, String note) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        Objects.requireNonNull(actor, "actor must not be null");
        currentStatus = newStatus;
        recordStatusChange(newStatus, actor.getId(), note);
    }

    public boolean filter(OrderFilterCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        return criteria.matches(this);
    }

    private void recordStatusChange(OrderStatus status, UUID actorId, String note) {
        statusHistory.add(new OrderStatusChange(status, LocalDateTime.now(), actorId, note));
    }

    private void recalculateTotal() {
        totalPrice = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
