package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.Order;
import com.naujokaitis.maistas.api.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientId(UUID clientId);
    List<Order> findByDriverId(UUID driverId);
    List<Order> findByRestaurantId(UUID restaurantId);
    List<Order> findByCurrentStatus(OrderStatus status);
    List<Order> findByDriverIsNullAndCurrentStatus(OrderStatus status);
}
