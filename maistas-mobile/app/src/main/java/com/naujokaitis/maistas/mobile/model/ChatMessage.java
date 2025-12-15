package com.naujokaitis.maistas.mobile.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage {
    private UUID id;
    private String authorId; // Using String/UUID for ID mapping to simplify
    private String authorName; // Extra field specifically for UI
    private String content;
    private String sentAt; // String for JSON serialization simplicity or LocalDateTime if handling adapter
    private MessageType messageType;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
}
