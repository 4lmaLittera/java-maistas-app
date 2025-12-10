package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import com.naujokaitis.maistas.database.CustomHibernate;
import java.time.LocalTime;
import java.util.*;

public class OrderDialog extends Dialog<Order> {

    private final ComboBox<Client> clientComboBox;
    private final ComboBox<Restaurant> restaurantComboBox;
    private final TextField deliveryAddressField;
    private final ListView<com.naujokaitis.maistas.model.MenuItem> availableItemsList;
    private final ListView<OrderItemEntry> selectedItemsList;
    private final Label totalLabel;
    private final ComboBox<PaymentType> paymentTypeComboBox;
    private final ComboBox<OrderStatus> statusComboBox;

    private final GenericHibernate<Client> clientRepo = new GenericHibernate<>(Client.class);
    private final GenericHibernate<Restaurant> restaurantRepo = new GenericHibernate<>(Restaurant.class);
    private final GenericHibernate<com.naujokaitis.maistas.model.MenuItem> menuItemRepo = new GenericHibernate<>(com.naujokaitis.maistas.model.MenuItem.class);
    private final CustomHibernate customHibernate = new CustomHibernate();

    private final List<OrderItemEntry> selectedItems = new ArrayList<>();
    private Order existingOrder;
    private Restaurant loadedRestaurant;

    public OrderDialog(Order order) {
        this.existingOrder = order;

        setTitle(order == null ? "Create Order" : "Edit Order");
        setHeaderText(order == null ? "Create a new order" : "Edit order status");

        // Create form fields
        clientComboBox = new ComboBox<>();
        clientComboBox.setPromptText("Select client");
        clientComboBox.setPrefWidth(200);

        restaurantComboBox = new ComboBox<>();
        restaurantComboBox.setPromptText("Select restaurant");
        restaurantComboBox.setPrefWidth(200);

        deliveryAddressField = new TextField();
        deliveryAddressField.setPromptText("Delivery address");
        deliveryAddressField.setPrefWidth(300);

        availableItemsList = new ListView<>();
        availableItemsList.setPrefHeight(150);
        availableItemsList.setPrefWidth(200);

        selectedItemsList = new ListView<>();
        selectedItemsList.setPrefHeight(150);
        selectedItemsList.setPrefWidth(250);

        totalLabel = new Label("Total: €0.00");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        paymentTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(PaymentType.values()));
        paymentTypeComboBox.setValue(PaymentType.CARD);

        statusComboBox = new ComboBox<>();

        // Load data
        loadClients();
        loadRestaurants();

        // Setup restaurant change listener
        restaurantComboBox.setOnAction(e -> loadMenuItems());

        // Setup client change listener to auto-fill address
        clientComboBox.setOnAction(e -> {
            Client selected = clientComboBox.getValue();
            if (selected != null && deliveryAddressField.getText().isEmpty()) {
                deliveryAddressField.setText(selected.getDefaultAddress());
            }
        });

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        if (order == null) {
            // Create mode
            grid.add(new Label("Client:*"), 0, 0);
            grid.add(clientComboBox, 1, 0);

            grid.add(new Label("Restaurant:*"), 0, 1);
            grid.add(restaurantComboBox, 1, 1);

            grid.add(new Label("Delivery Address:*"), 0, 2);
            grid.add(deliveryAddressField, 1, 2);

            grid.add(new Label("Payment Method:"), 0, 3);
            grid.add(paymentTypeComboBox, 1, 3);

            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser instanceof Client) {
                Client appClient = (Client) currentUser;
                
                clientComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(appClient.getId()))
                    .findFirst()
                    .ifPresent(c -> {
                        clientComboBox.setValue(c);
                        clientComboBox.setDisable(true);
                        // Trigger address fill manually since we set value programmatically
                        if (deliveryAddressField.getText().isEmpty()) {
                            deliveryAddressField.setText(c.getDefaultAddress());
                        }
                    });

                // Set default payment method
                for (PaymentMethod pm : appClient.getPaymentMethods()) {
                    if (pm.isDefault()) {
                        paymentTypeComboBox.setValue(pm.getPaymentType());
                        break;
                    }
                }
            }

            // Items selection area
            VBox itemsBox = new VBox(10);
            itemsBox.setPadding(new Insets(10, 0, 0, 0));

            Label availableLabel = new Label("Available Items:");
            Label selectedLabel = new Label("Selected Items:");

            Button addItemBtn = new Button("Add →");
            addItemBtn.setOnAction(e -> addSelectedItem());

            Button removeItemBtn = new Button("← Remove");
            removeItemBtn.setOnAction(e -> removeSelectedItem());

            VBox buttonsBox = new VBox(5);
            buttonsBox.getChildren().addAll(addItemBtn, removeItemBtn);

            HBox listsBox = new HBox(10);
            VBox availableBox = new VBox(5, availableLabel, availableItemsList);
            VBox selectedBox = new VBox(5, selectedLabel, selectedItemsList);
            listsBox.getChildren().addAll(availableBox, buttonsBox, selectedBox);

            itemsBox.getChildren().addAll(listsBox, totalLabel);

            grid.add(itemsBox, 0, 4, 2, 1);
        } else {
            // Edit mode - only status change
            Label idLabel = new Label("Order ID:");
            TextField idField = new TextField(order.getId().toString().substring(0, 8));
            idField.setEditable(false);

            Label clientLabel = new Label("Client:");
            TextField clientField = new TextField(order.getClient().getUsername());
            clientField.setEditable(false);

            Label restaurantLabel = new Label("Restaurant:");
            TextField restaurantField = new TextField(order.getRestaurant().getName());
            restaurantField.setEditable(false);

            Label currentStatusLabel = new Label("Current Status:");
            
            // Populate status based on role
            User currentUser = Session.getInstance().getCurrentUser();
            List<OrderStatus> allowedStatuses = new ArrayList<>();
            
            if (currentUser instanceof com.naujokaitis.maistas.model.Administrator) {
                 allowedStatuses.addAll(Arrays.asList(OrderStatus.values()));
            } else if (currentUser instanceof com.naujokaitis.maistas.model.RestaurantOwner) {
                 allowedStatuses.add(OrderStatus.CONFIRMED);
                 allowedStatuses.add(OrderStatus.PREPARING);
                 allowedStatuses.add(OrderStatus.READY);
            } else if (currentUser instanceof Driver) {
                 allowedStatuses.add(OrderStatus.PICKED_UP);
                 allowedStatuses.add(OrderStatus.DELIVERED);
            }
            
            // Ensure current status is present so it doesn't look blank
            if (!allowedStatuses.contains(order.getCurrentStatus())) {
                 allowedStatuses.add(0, order.getCurrentStatus());
            }

            statusComboBox.setItems(FXCollections.observableArrayList(allowedStatuses));
            statusComboBox.setValue(order.getCurrentStatus());
            
            if (currentUser instanceof Client) {
                 statusComboBox.setDisable(true);
            }

            grid.add(idLabel, 0, 0);
            grid.add(idField, 1, 0);
            grid.add(clientLabel, 0, 1);
            grid.add(clientField, 1, 1);
            grid.add(restaurantLabel, 0, 2);
            grid.add(restaurantField, 1, 2);
            grid.add(currentStatusLabel, 0, 3);
            grid.add(statusComboBox, 1, 3);
        }

        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType(order == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createOrUpdateOrder();
            }
            return null;
        });
    }

    private void loadClients() {
        try {
            List<Client> clients = clientRepo.findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
            clientComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getUsername() + " - " + client.getEmail());
                }
            });
            clientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getUsername());
                }
            });
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantRepo.findAll();
            // Filter restaurants that have menus with items
            List<Restaurant> restaurantsWithMenu = restaurants.stream()
                    .filter(r -> r.getMenu() != null && !r.getMenu().getItems().isEmpty())
                    .toList();

            restaurantComboBox.setItems(FXCollections.observableArrayList(restaurantsWithMenu));
            restaurantComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Restaurant restaurant, boolean empty) {
                    super.updateItem(restaurant, empty);
                    setText(empty || restaurant == null ? null
                            : restaurant.getName() + " - " + restaurant.getAddress());
                }
            });
            restaurantComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Restaurant restaurant, boolean empty) {
                    super.updateItem(restaurant, empty);
                    setText(empty || restaurant == null ? null : restaurant.getName());
                }
            });
        } catch (Exception e) {
            showError("Failed to load restaurants: " + e.getMessage());
        }
    }

    private void loadMenuItems() {
        availableItemsList.getItems().clear();
        Restaurant selected = restaurantComboBox.getValue();
        if (selected != null) {
            // Load full restaurant with pricing rules
            loadedRestaurant = customHibernate.findRestaurantWithPricingRules(selected.getId());
            
            if (loadedRestaurant != null && loadedRestaurant.getMenu() != null) {
                availableItemsList.setItems(FXCollections.observableArrayList(loadedRestaurant.getMenu().getItems()));
                availableItemsList.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(com.naujokaitis.maistas.model.MenuItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Calculate price for display if rules apply
                            BigDecimal price = calculatePrice(item);
                            String priceText = "€" + price;
                            if (price.compareTo(item.getPrice()) != 0) {
                                priceText += " (Regular: €" + item.getPrice() + ")";
                            }
                            setText(item.getName() + " - " + priceText);
                        }
                    }
                });
            }
        }
    }

    private BigDecimal calculatePrice(com.naujokaitis.maistas.model.MenuItem item) {
        BigDecimal price = item.getPrice();
        if (loadedRestaurant != null) {
            LocalTime now = LocalTime.now();
            for (PricingRule rule : loadedRestaurant.getPricingRules()) {
                if (rule.getTimeRange() != null && rule.getTimeRange().contains(now)) {
                    price = rule.apply(item, price);
                }
            }
        }
        return price;
    }

    private void addSelectedItem() {
        com.naujokaitis.maistas.model.MenuItem selected = availableItemsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        // Check if already added
        Optional<OrderItemEntry> existing = selectedItems.stream()
                .filter(e -> e.menuItem.getId().equals(selected.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().quantity++;
        } else {
            BigDecimal finalPrice = calculatePrice(selected);
            selectedItems.add(new OrderItemEntry(selected, 1, finalPrice));
        }

        refreshSelectedItems();
        updateTotal();
    }

    private void removeSelectedItem() {
        OrderItemEntry selected = selectedItemsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        if (selected.quantity > 1) {
            selected.quantity--;
        } else {
            selectedItems.remove(selected);
        }

        refreshSelectedItems();
        updateTotal();
    }

    private void refreshSelectedItems() {
        selectedItemsList.setItems(FXCollections.observableArrayList(selectedItems));
        selectedItemsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(OrderItemEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                setText(empty || entry == null ? null
                        : entry.quantity + "x " + entry.menuItem.getName() + " - €" +
                                entry.unitPrice.multiply(BigDecimal.valueOf(entry.quantity)) + 
                                (entry.unitPrice.compareTo(entry.menuItem.getPrice()) != 0 ? " (Modified)" : ""));
            }
        });
    }

    private void updateTotal() {
        BigDecimal total = selectedItems.stream()
                .map(e -> e.unitPrice.multiply(BigDecimal.valueOf(e.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText("Total: €" + total);
    }

    private boolean validateInput() {
        if (existingOrder != null) {
            return true; // Edit mode doesn't need validation
        }

        StringBuilder errors = new StringBuilder();

        if (clientComboBox.getValue() == null) {
            errors.append("Please select a client\n");
        }
        if (restaurantComboBox.getValue() == null) {
            errors.append("Please select a restaurant\n");
        }
        if (deliveryAddressField.getText().trim().isEmpty()) {
            errors.append("Delivery address is required\n");
        }
        if (selectedItems.isEmpty()) {
            errors.append("Please add at least one item to the order\n");
        }

        // Check inventory availability
        for (OrderItemEntry entry : selectedItems) {
            if (entry.quantity > entry.menuItem.getInventoryCount()) {
                errors.append("Insufficient inventory for ").append(entry.menuItem.getName())
                      .append(". Available: ").append(entry.menuItem.getInventoryCount()).append("\n");
            }
        }

        if (paymentTypeComboBox.getValue() == PaymentType.WALLET) {
            Client client = clientComboBox.getValue();
            if (client != null) {
                BigDecimal total = selectedItems.stream()
                        .map(e -> e.unitPrice.multiply(BigDecimal.valueOf(e.quantity)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (client.getWalletBalance().compareTo(total) < 0) {
                    errors.append("Insufficient funds in wallet. Balance: €").append(client.getWalletBalance()).append("\n");
                }
            }
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        return true;
    }

    private Order createOrUpdateOrder() {
        if (existingOrder != null) {
            OrderStatus newStatus = statusComboBox.getValue();
            if (newStatus != null && newStatus != existingOrder.getCurrentStatus()) {
                 User currentUser = Session.getInstance().getCurrentUser();
                 existingOrder.updateStatus(newStatus, currentUser, "Status updated by user");
                 
                 // Award loyalty points if delivered
                 if (newStatus == OrderStatus.DELIVERED) {
                     Client client = existingOrder.getClient();
                     if (client != null) {
                         int pointsEarned = existingOrder.getTotalPrice().multiply(new BigDecimal("10")).intValue();
                         LoyaltyAccount account = new LoyaltyAccount(client, client.getLoyaltyPoints(), com.naujokaitis.maistas.model.LoyaltyTier.BRONZE);
                         account.earnPoints(existingOrder, pointsEarned);
                         client.setLoyaltyPoints(account.getPointsBalance());
                         
                         try {
                             clientRepo.update(client);
                             System.out.println(" awarded " + pointsEarned + " points to " + client.getUsername());
                         } catch (Exception e) {
                             showError("Failed to update loyalty points: " + e.getMessage());
                         }
                     }
                 }
            }
            return existingOrder;
        }

        Client client = clientComboBox.getValue();
        Restaurant restaurant = restaurantComboBox.getValue();
        String address = deliveryAddressField.getText().trim();
        PaymentType paymentType = paymentTypeComboBox.getValue();

        Order order = new Order(UUID.randomUUID(), client, restaurant, address, paymentType);

        for (OrderItemEntry entry : selectedItems) {
            OrderItem orderItem = new OrderItem(
                    UUID.randomUUID(),
                    entry.menuItem,
                    entry.quantity,
                    entry.unitPrice,
                    null);
            order.addItem(orderItem);
            
            // Reduce inventory
            com.naujokaitis.maistas.model.MenuItem itemToUpdate = entry.menuItem;
            itemToUpdate.setInventoryCount(itemToUpdate.getInventoryCount() - entry.quantity);
            try {
                menuItemRepo.update(itemToUpdate);
            } catch (Exception e) {
                // If update fails, we should ideally rollback the whole order creation,
                // but for now let's just log/show error.
                // In a real app we'd wrap this all in one transaction check.
                e.printStackTrace();
                showError("Failed to update inventory for " + itemToUpdate.getName());
            }
        }

        return order;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<Order> showCreateDialog() {
        OrderDialog dialog = new OrderDialog(null);
        return dialog.showAndWait();
    }

    public static Optional<Order> showEditDialog(Order order) {
        OrderDialog dialog = new OrderDialog(order);
        return dialog.showAndWait();
    }

    private static class OrderItemEntry {
        com.naujokaitis.maistas.model.MenuItem menuItem;
        int quantity;
        BigDecimal unitPrice;

        OrderItemEntry(com.naujokaitis.maistas.model.MenuItem menuItem, int quantity, BigDecimal unitPrice) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}
