package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.CustomHibernate;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChatDialog extends Dialog<Void> {

    private final Order order;
    private final GenericHibernate<ChatThread> chatThreadRepo = new GenericHibernate<>(ChatThread.class);
    private final GenericHibernate<ChatMessage> messageRepo = new GenericHibernate<>(ChatMessage.class);
    private final GenericHibernate<Order> orderRepo = new GenericHibernate<>(Order.class);

    private ChatThread currentThread;
    private final ListView<ChatMessage> messageList;
    private final TextArea messageInput;

    public ChatDialog(Order order) {
        this.order = order;
        setTitle("Chat - Order #" + order.getId().toString().substring(0, 8));

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(400);
        content.setPrefHeight(500);

        // Initialize or load chat thread
        loadChatThread();

        // Message List
        messageList = new ListView<>();
        messageList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(2);
                    Label authorLabel = new Label(msg.getAuthor().getUsername());
                    authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
                    Label contentLabel = new Label(msg.getContent());
                    contentLabel.setWrapText(true);
                    Label timeLabel = new Label(msg.getSentAt().getHour() + ":" + msg.getSentAt().getMinute());
                    timeLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #888;");

                    box.getChildren().addAll(authorLabel, contentLabel, timeLabel);

                    // Align based on current user
                    User currentUser = Session.getInstance().getCurrentUser();
                    if (currentUser != null && msg.getAuthor().getId().equals(currentUser.getId())) {
                        box.setAlignment(Pos.CENTER_RIGHT);
                        authorLabel.setAlignment(Pos.CENTER_RIGHT);
                        contentLabel
                                .setStyle("-fx-background-color: #e3f2fd; -fx-padding: 5; -fx-background-radius: 5;");
                    } else {
                        box.setAlignment(Pos.CENTER_LEFT);
                        contentLabel
                                .setStyle("-fx-background-color: #f5f5f5; -fx-padding: 5; -fx-background-radius: 5;");
                    }
                    setGraphic(box);
                }
            }
        });

        // Input area
        HBox inputBox = new HBox(10);
        messageInput = new TextArea();
        messageInput.setPromptText("Type a message...");
        messageInput.setPrefRowCount(2);
        messageInput.setPrefWidth(300);

        Button sendBtn = new Button("Send");
        sendBtn.setPrefHeight(50);
        sendBtn.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(messageInput, sendBtn);

        content.getChildren().addAll(messageList, inputBox);
        getDialogPane().setContent(content);

        refreshMessages();
    }

    private void loadChatThread() {
        // Try to find existing thread for this order
        List<ChatThread> threads = chatThreadRepo.findAll();
        currentThread = threads.stream()
                .filter(t -> t.getOrder() != null && t.getOrder().getId().equals(order.getId()))
                .findFirst()
                .orElse(null);

        if (currentThread == null) {
            // Create new thread
            currentThread = new ChatThread(UUID.randomUUID(), order, List.of(order.getClient()));
            // Add restaurant owner if possible, but for now just client
            chatThreadRepo.save(currentThread);
            
            // Link to order and update order (owning side)
            order.setChatThread(currentThread);
            orderRepo.update(order);
        }
    }

    private final CustomHibernate customHibernate = new CustomHibernate();

    private void refreshMessages() {
        if (currentThread != null) {
            // Reload thread to get messages with eager fetch to avoid LazyInitializationException
            currentThread = customHibernate.findChatThreadWithMessages(currentThread.getId());
            if (currentThread != null) {
                messageList.setItems(FXCollections.observableArrayList(currentThread.getMessages()));
                if (!messageList.getItems().isEmpty()) {
                    messageList.scrollTo(messageList.getItems().size() - 1);
                }
            }
        }
    }

    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (content.isEmpty())
            return;

        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        try {
            ChatMessage message = new ChatMessage(
                    UUID.randomUUID(),
                    currentUser,
                    content,
                    LocalDateTime.now(),
                    MessageType.TEXT,
                    currentThread);

            messageRepo.save(message);

            // Also update thread's list in memory/db if needed, but saving message with
            // relation should be enough
            // if we refresh.

            messageInput.clear();
            refreshMessages();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to send message: " + e.getMessage());
            alert.show();
        }
    }
}
