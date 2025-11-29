package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ChatThread {

    private final UUID id;
    private final Order order;
    @Getter(AccessLevel.NONE)
    private final List<User> participants;
    @Getter(AccessLevel.NONE)
    private final List<ChatMessage> messages;

    public ChatThread(UUID id, Order order, List<User> participants) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.order = Objects.requireNonNull(order, "order must not be null");
        this.participants = new ArrayList<>(Objects.requireNonNullElse(participants, List.of()));
        this.messages = new ArrayList<>();
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

