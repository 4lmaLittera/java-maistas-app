package com.naujokaitis.maistas.mobile.model;

import java.util.List;
import java.util.UUID;

public class ChatThread {
    private UUID id;
    private UUID orderId; // Simplified mapping
    private List<ChatMessage> messages;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}
