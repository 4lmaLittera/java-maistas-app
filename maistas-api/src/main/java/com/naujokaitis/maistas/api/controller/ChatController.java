package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.*;
import com.naujokaitis.maistas.api.repository.ChatMessageRepository;
import com.naujokaitis.maistas.api.repository.ChatThreadRepository;
import com.naujokaitis.maistas.api.repository.OrderRepository;
import com.naujokaitis.maistas.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatThreadRepository chatThreadRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // GET - Get chat thread by Order ID
    @GetMapping("/order/{orderId}")
    public EntityModel<ChatThread> getChatByOrder(@PathVariable UUID orderId) {
        ChatThread thread = chatThreadRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatThread not found for order", orderId));

        return EntityModel.of(thread,
                linkTo(methodOn(ChatController.class).getChatByOrder(orderId)).withSelfRel(),
                linkTo(methodOn(ChatController.class).getMessages(thread.getId())).withRel("messages"));
    }

    // POST - Start or Get Chat Thread
    @PostMapping("/start/{orderId}")
    public EntityModel<ChatThread> startChat(@PathVariable UUID orderId, @RequestParam UUID initiatorId) {
        return chatThreadRepository.findByOrderId(orderId)
                .map(thread -> EntityModel.of(thread,
                        linkTo(methodOn(ChatController.class).getChatByOrder(orderId)).withSelfRel()))
                .orElseGet(() -> {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
                    User initiator = userRepository.findById(initiatorId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", initiatorId));

                    List<User> participants = new ArrayList<>();
                    participants.add(initiator);
                    // Optionally add other parties (Restaurant Owner, Driver, Client) based on Order
                    if (order.getClient() != null && !participants.contains(order.getClient())) {
                        participants.add(order.getClient());
                    }
                    if (order.getDriver() != null && !participants.contains(order.getDriver())) {
                        participants.add(order.getDriver());
                    }
                     // Restaurant owner adding logic would be complex without direct link, skipping for now or adding later

                    ChatThread newThread = new ChatThread(UUID.randomUUID(), order, participants);
                    // chatThreadRepository.save(newThread); // REMOVED: Rely on cascade from Order
                    
                    // Link back and generic save
                    order.setChatThread(newThread);
                    orderRepository.save(order);

                    return EntityModel.of(newThread,
                            linkTo(methodOn(ChatController.class).getChatByOrder(orderId)).withSelfRel());
                });
    }

    // GET - Get messages for a thread
    @GetMapping("/{threadId}/messages")
    public List<ChatMessage> getMessages(@PathVariable UUID threadId) {
        return chatMessageRepository.findByChatThreadIdOrderBySentAtAsc(threadId);
    }

    // POST - Send a message
    @PostMapping("/{threadId}/message")
    public ChatMessage sendMessage(@PathVariable UUID threadId, @RequestBody MessageRequest request) {
        ChatThread thread = chatThreadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatThread", threadId));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getAuthorId()));

        ChatMessage message = new ChatMessage(
                UUID.randomUUID(),
                author,
                request.getContent(),
                LocalDateTime.now(),
                request.getMessageType() != null ? request.getMessageType() : MessageType.TEXT,
                thread
        );

        return chatMessageRepository.save(message);
    }

    // DTO
    public static class MessageRequest {
        private UUID authorId;
        private String content;
        private MessageType messageType;

        public UUID getAuthorId() { return authorId; }
        public void setAuthorId(UUID authorId) { this.authorId = authorId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public MessageType getMessageType() { return messageType; }
        public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    }
}
