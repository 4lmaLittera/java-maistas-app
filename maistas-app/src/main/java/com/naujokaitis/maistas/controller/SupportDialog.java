package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.CustomHibernate;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupportDialog extends Dialog<Void> {

    private final Order order;
    private final GenericHibernate<SupportTicket> ticketRepo = new GenericHibernate<>(SupportTicket.class);
    private final CustomHibernate customRepo = new CustomHibernate();
    private ListView<SupportTicket> ticketList;
    private Button resolveBtn;
    private Button closeBtn;

    public SupportDialog(Order order) {
        this.order = order;
        setupDialog();
    }

    private void setupDialog() {
        setTitle("Support Tickets - Order " + order.getId().toString().substring(0, 8));
        setHeaderText("Manage Support Tickets");

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(500);
        content.setPrefHeight(600);

        // List of support tickets
        ticketList = new ListView<>();
        ticketList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(SupportTicket ticket, boolean empty) {
                super.updateItem(ticket, empty);
                if (empty || ticket == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    String statusStr = ticket.getStatus() != null ? ticket.getStatus().toString() : "UNKNOWN";
                    Label headerLabel = new Label("Status: " + statusStr);
                    headerLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label userLabel = new Label("Created by: " + (ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUsername() : "Unknown"));
                    Label descLabel = new Label("Description: " + ticket.getDescription());
                    descLabel.setWrapText(true);
                    
                    box.getChildren().addAll(headerLabel, userLabel, descLabel);
                    
                    if (ticket.getResolution() != null && !ticket.getResolution().isEmpty()) {
                        Label resLabel = new Label("Resolution: " + ticket.getResolution());
                        resLabel.setStyle("-fx-text-fill: green;");
                        resLabel.setWrapText(true);
                        box.getChildren().add(resLabel);
                    }
                    
                    setGraphic(box);
                }
            }
        });
        loadTickets();

        // Action buttons
        resolveBtn = new Button("Resolve");
        resolveBtn.setVisible(false);
        resolveBtn.managedProperty().bind(resolveBtn.visibleProperty());
        resolveBtn.setOnAction(e -> handleResolveTicket());

        closeBtn = new Button("Close Ticket");
        closeBtn.setVisible(false);
        closeBtn.managedProperty().bind(closeBtn.visibleProperty());
        closeBtn.setOnAction(e -> handleCloseTicket());

        // Delete button (Admin only)
        Button deleteBtn = new Button("Delete");
        deleteBtn.setVisible(false);
        deleteBtn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red; -fx-border-color: red;");
        deleteBtn.managedProperty().bind(deleteBtn.visibleProperty());
        deleteBtn.setOnAction(e -> handleDeleteTicket());

        HBox actionButtons = new HBox(10, resolveBtn, closeBtn, deleteBtn);

        // Update button states
        ticketList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonStates(newVal, deleteBtn);
        });

        // Add New Ticket Pane
        TitledPane addTicketPane = new TitledPane();
        addTicketPane.setText("Create New Ticket");
        addTicketPane.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the issue...");
        descArea.setPrefRowCount(3);

        Button submitBtn = new Button("Submit Ticket");
        submitBtn.setOnAction(e -> {
            if (descArea.getText() != null && !descArea.getText().trim().isEmpty()) {
                createTicket(descArea.getText().trim());
                descArea.clear();
                loadTickets();
            } else {
                showAlert("Please enter a description.");
            }
        });

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descArea, 1, 0);
        grid.add(submitBtn, 1, 1);
        addTicketPane.setContent(grid);

        content.getChildren().addAll(new Label("Existing Tickets:"), ticketList, actionButtons, addTicketPane);
        
        getDialogPane().setContent(content);
    }

    private void updateButtonStates(SupportTicket selectedTicket, Button deleteBtn) {
        User currentUser = Session.getInstance().getCurrentUser();
        if (selectedTicket == null || currentUser == null) {
            resolveBtn.setVisible(false);
            closeBtn.setVisible(false);
            deleteBtn.setVisible(false);
            return;
        }

        boolean isOpen = selectedTicket.getStatus() == TicketStatus.OPEN || selectedTicket.getStatus() == TicketStatus.IN_PROGRESS;
        boolean isCreator = selectedTicket.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser instanceof Administrator;

        // Resolve: Only Admin can resolve, if open
        resolveBtn.setVisible(isAdmin && isOpen);

        // Close: Creator or Admin can close/cancel
        closeBtn.setVisible(isOpen && (isCreator || isAdmin));

        // Delete: Only Admin
        deleteBtn.setVisible(isAdmin);
    }
    
    private void handleDeleteTicket() {
        SupportTicket selected = ticketList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Ticket");
        alert.setHeaderText("Delete Support Ticket?");
        alert.setContentText("Are you sure you want to permanently delete this ticket?");
        
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    ticketRepo.delete(selected);
                    loadTickets();
                } catch (Exception e) {
                    showAlert("Failed to delete ticket: " + e.getMessage());
                }
            }
        });
    }

    private void handleResolveTicket() {
        SupportTicket selected = ticketList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Resolve Ticket");
        dialog.setHeaderText("Enter resolution details:");
        dialog.setContentText("Resolution:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(resolution -> {
             if (!resolution.trim().isEmpty()) {
                 selected.resolve(resolution.trim());
                 try {
                     ticketRepo.update(selected);
                     loadTickets();
                 } catch (Exception e) {
                     showAlert("Failed to resolve ticket: " + e.getMessage());
                 }
             }
        });
    }

    private void handleCloseTicket() {
        SupportTicket selected = ticketList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Ticket");
        alert.setHeaderText("Are you sure you want to close this ticket?");
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                selected.close();
                try {
                    ticketRepo.update(selected);
                    loadTickets();
                } catch (Exception e) {
                    showAlert("Failed to close ticket: " + e.getMessage());
                }
            }
        });
    }

    private void createTicket(String description) {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("You must be logged in.");
            return;
        }

        try {
            SupportTicket ticket = new SupportTicket(
                    UUID.randomUUID(),
                    order,
                    currentUser,
                    description
            );
            ticketRepo.save(ticket);
        } catch (Exception e) {
            showAlert("Failed to create ticket: " + e.getMessage());
        }
    }

    private void loadTickets() {
        try {
            List<SupportTicket> tickets = customRepo.findSupportTicketsByOrderId(order.getId());
            if (tickets != null) {
                ticketList.setItems(FXCollections.observableArrayList(tickets));
            }
        } catch (Exception e) {
            showAlert("Failed to load tickets: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
