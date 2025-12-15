package com.naujokaitis.maistas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "menu"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_thread_id", referencedColumnName = "id")
    private ChatThread chatThread;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "placed_at", nullable = false)
    private LocalDateTime placedAt;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private OrderStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    public Order(UUID id, Client client, Restaurant restaurant, String deliveryAddress, PaymentType paymentType) {
        this.id = id;
        this.client = client;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.paymentType = paymentType != null ? paymentType : PaymentType.CARD;
        this.items = new ArrayList<>();
        this.totalPrice = BigDecimal.ZERO;
        this.placedAt = LocalDateTime.now();
        this.currentStatus = OrderStatus.CREATED;
    }

    public static Order create(Client client, Restaurant restaurant, String deliveryAddress, PaymentType paymentType) {
        return new Order(UUID.randomUUID(), client, restaurant, deliveryAddress, paymentType);
    }

    public void addItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.currentStatus = newStatus;
    }

    private void recalculateTotal() {
        totalPrice = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
