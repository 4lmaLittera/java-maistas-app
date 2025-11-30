package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "chat_threads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatThread {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(mappedBy = "chatThread")
    private Order order;

    @Getter(AccessLevel.NONE)
    @ManyToMany
    @JoinTable(name = "chat_participants", joinColumns = @JoinColumn(name = "chat_thread_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "chatThread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatThread(UUID id, Order order, List<User> participants) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.order = order;
        this.participants = new ArrayList<>(Objects.requireNonNullElse(participants, List.of()));
    }

    public List<User> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addParticipant(User user) {
        participants.add(Objects.requireNonNull(user, "user must not be null"));
    }

    public void addMessage(ChatMessage message) {
        messages.add(Objects.requireNonNull(message, "message must not be null"));
    }
}
