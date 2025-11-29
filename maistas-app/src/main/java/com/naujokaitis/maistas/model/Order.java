package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Order {

    private final UUID id;
    private final Client client;
    private final Restaurant restaurant;
    private Driver driver;
    @Getter(AccessLevel.NONE)
    private final List<OrderItem> items;
    @Getter(AccessLevel.NONE)
    private final List<OrderStatusChange> statusHistory;
    @Setter
    private DeliveryStatus deliveryStatus;
    @Setter
    private ChatThread chatThread;
    private BigDecimal totalPrice;
    private final LocalDateTime placedAt;
    @Setter
    @NonNull
    private String deliveryAddress;
    private OrderStatus currentStatus;

    public Order(UUID id,
                 Client client,
                 Restaurant restaurant,
                 String deliveryAddress) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant must not be null");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress must not be null");
        this.items = new ArrayList<>();
        this.statusHistory = new ArrayList<>();
        this.totalPrice = BigDecimal.ZERO;
        this.placedAt = LocalDateTime.now();
        this.currentStatus = OrderStatus.CREATED;
        recordStatusChange(OrderStatus.CREATED, client, "Order created");
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
        recordStatusChange(newStatus, actor, note);
    }

    public boolean filter(OrderFilterCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        return criteria.matches(this);
    }

    private void recordStatusChange(OrderStatus status, User actor, String note) {
        statusHistory.add(new OrderStatusChange(status, LocalDateTime.now(), actor, note));
    }

    private void recalculateTotal() {
        totalPrice = items.stream()
                          .map(OrderItem::getSubtotal)
                          .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
