package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ChatMessage {

    private final UUID id;
    private final User author;
    private final String content;
    private final LocalDateTime sentAt;
    private final MessageType messageType;

    public ChatMessage(UUID id,
                       User author,
                       String content,
                       LocalDateTime sentAt,
                       MessageType messageType) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.content = Objects.requireNonNull(content, "content must not be null");
        this.sentAt = Objects.requireNonNullElse(sentAt, LocalDateTime.now());
        this.messageType = Objects.requireNonNull(messageType, "messageType must not be null");
    }

}

