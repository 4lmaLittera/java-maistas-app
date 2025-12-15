package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
