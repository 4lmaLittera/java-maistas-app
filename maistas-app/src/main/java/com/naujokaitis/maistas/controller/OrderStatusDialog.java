package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class OrderStatusDialog extends Dialog<OrderStatus> {

    private final ComboBox<OrderStatus> statusComboBox;
    private final TextArea noteArea;
    private final Order order;

    public OrderStatusDialog(Order order) {
        this.order = order;

        setTitle("Update Order Status");
        setHeaderText("Change status for Order #" + order.getId().toString().substring(0, 8));

        // Create form fields
        statusComboBox = new ComboBox<>();
        
        // Populate status based on role
        User currentUser = Session.getInstance().getCurrentUser();
        java.util.List<OrderStatus> allowedStatuses = new java.util.ArrayList<>();
        
        if (currentUser instanceof Administrator) {
             allowedStatuses.addAll(java.util.Arrays.asList(OrderStatus.values()));
        } else if (currentUser instanceof RestaurantOwner) {
             // Restrict to logical restaurant steps
             allowedStatuses.add(OrderStatus.CONFIRMED);
             allowedStatuses.add(OrderStatus.PREPARING);
             allowedStatuses.add(OrderStatus.READY);
        } else if (currentUser instanceof Driver) {
             allowedStatuses.add(OrderStatus.PICKED_UP);
             allowedStatuses.add(OrderStatus.DELIVERED);
        }
        
        // Ensure current status is present so it doesn't look blank (but maybe disable if not allowed to switch back?)
        // Taking a safe approach: add current so it displays correctly.
        if (!allowedStatuses.contains(order.getCurrentStatus())) {
             allowedStatuses.add(0, order.getCurrentStatus());
        }

        statusComboBox.setItems(FXCollections.observableArrayList(allowedStatuses));
        statusComboBox.setValue(order.getCurrentStatus());
        statusComboBox.setPrefWidth(200);

        noteArea = new TextArea();
        noteArea.setPromptText("Add a note (optional)");
        noteArea.setPrefRowCount(3);
        noteArea.setPrefWidth(300);

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("Current Status:"), 0, 0);
        grid.add(new Label(order.getCurrentStatus().toString()), 1, 0);

        grid.add(new Label("New Status:*"), 0, 1);
        grid.add(statusComboBox, 1, 1);

        grid.add(new Label("Note:"), 0, 2);
        grid.add(noteArea, 1, 2);

        // Order details
        grid.add(new Label("Client:"), 0, 3);
        grid.add(new Label(order.getClient().getUsername()), 1, 3);

        grid.add(new Label("Restaurant:"), 0, 4);
        grid.add(new Label(order.getRestaurant().getName()), 1, 4);

        grid.add(new Label("Total:"), 0, 5);
        // Item removal section for Restaurants/Admins
        if (currentUser.getRole() == UserRole.RESTAURANT_OWNER || currentUser.getRole() == UserRole.ADMIN) {
            Label itemsLabel = new Label("Order Items:");
            ListView<OrderItem> itemsListView = new ListView<>();
            itemsListView.setPrefHeight(100);
            itemsListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(OrderItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%dx %s - €%.2f", item.getQuantity(), item.getMenuItem().getName(), item.getSubtotal()));
                    }
                }
            });
            
            // Load items
            System.out.println("DEBUG: Order items size: " + order.getItems().size());
            if (order.getItems().isEmpty()) {
                itemsListView.setPlaceholder(new Label("No items in this order."));
            }
            itemsListView.setItems(FXCollections.observableArrayList(order.getItems()));
            
            Label totalValueLabel = new Label("€" + order.getTotalPrice());
            grid.add(totalValueLabel, 1, 5);
            
            Button removeItemBtn = new Button("Remove Selected Item");
            removeItemBtn.setDisable(true);
            
            itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                removeItemBtn.setDisable(newVal == null);
            });
            
            removeItemBtn.setOnAction(e -> {
                OrderItem selectedItem = itemsListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    order.removeItem(selectedItem);
                    itemsListView.setItems(FXCollections.observableArrayList(order.getItems())); // Refresh
                    totalValueLabel.setText("€" + order.getTotalPrice());
                }
            });

            grid.add(itemsLabel, 0, 6);
            grid.add(itemsListView, 0, 7, 2, 1);
            grid.add(removeItemBtn, 0, 8);
        } else {
            grid.add(new Label("€" + order.getTotalPrice()), 1, 5);
        }

        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusComboBox.getValue();
            }
            return null;
        });
    }

    public String getNote() {
        return noteArea.getText().trim();
    }

    public static Optional<OrderStatus> showDialog(Order order) {
        OrderStatusDialog dialog = new OrderStatusDialog(order);
        return dialog.showAndWait();
    }
}
