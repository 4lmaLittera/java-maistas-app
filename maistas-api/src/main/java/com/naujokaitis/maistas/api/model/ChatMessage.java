package com.naujokaitis.maistas.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_thread_id", nullable = false)
    private ChatThread chatThread;

    public ChatMessage(UUID id,
            User author,
            String content,
            LocalDateTime sentAt,
            MessageType messageType,
            ChatThread chatThread) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.content = Objects.requireNonNull(content, "content must not be null");
        this.sentAt = Objects.requireNonNullElse(sentAt, LocalDateTime.now());
        this.messageType = Objects.requireNonNull(messageType, "messageType must not be null");
        this.chatThread = chatThread;
    }

}
