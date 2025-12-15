package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatThreadRepository extends JpaRepository<ChatThread, UUID> {
    Optional<ChatThread> findByOrderId(UUID orderId);
}
