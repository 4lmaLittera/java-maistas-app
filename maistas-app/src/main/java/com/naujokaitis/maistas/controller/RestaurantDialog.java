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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RestaurantDialog extends Dialog<Restaurant> {

    private final TextField nameField;
    private final TextField addressField;
    private final TextArea descriptionArea;
    private final Label ratingLabel;
    private final ComboBox<RestaurantOwner> ownerComboBox;

    // New fields for schedule and pricing
    private OperatingSchedule operatingSchedule;
    private final List<PricingRule> pricingRules = new ArrayList<>();
    private final ListView<PricingRule> pricingRulesList;

    private final GenericHibernate<RestaurantOwner> ownerRepo = new GenericHibernate<>(RestaurantOwner.class);
    private Restaurant existingRestaurant;

    public RestaurantDialog(Restaurant restaurant) {
        this.existingRestaurant = restaurant;
        if (restaurant != null) {
            this.operatingSchedule = restaurant.getOperatingHours();
            this.pricingRules.addAll(restaurant.getPricingRules());
        }

        setTitle(restaurant == null ? "Add Restaurant" : "Edit Restaurant");
        setHeaderText(restaurant == null ? "Enter restaurant details" : "Update restaurant details");

        // Create form fields
        nameField = new TextField();
        nameField.setPromptText("Restaurant name");

        addressField = new TextField();
        addressField.setPromptText("Full address");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setPrefRowCount(3);

        ratingLabel = new Label("(Calculated from reviews)");
        ratingLabel.setStyle("-fx-text-fill: #666;");

        ownerComboBox = new ComboBox<>();
        ownerComboBox.setPromptText("Select owner");
        loadOwners();

        // Pricing Rules List
        pricingRulesList = new ListView<>();
        pricingRulesList.setPrefHeight(100);
        updatePricingRulesList();

        // Setup TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: General Info
        Tab generalTab = new Tab("General Info");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Address:*"), 0, 1);
        grid.add(addressField, 1, 1);

        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);

        grid.add(new Label("Rating:"), 0, 3);
        grid.add(ratingLabel, 1, 3);

        grid.add(new Label("Owner:*"), 0, 4);
        grid.add(ownerComboBox, 1, 4);

        generalTab.setContent(grid);

        // Tab 2: Operating Hours & Pricing
        Tab advancedTab = new Tab("Schedule & Pricing");
        VBox advancedBox = new VBox(10);
        advancedBox.setPadding(new Insets(20));

        // Operating Hours Section
        Label scheduleLabel = new Label("Operating Hours");
        scheduleLabel.setStyle("-fx-font-weight: bold");
        Button scheduleBtn = new Button("Manage Operating Hours");
        scheduleBtn.setOnAction(e -> {
            new OperatingHoursDialog(operatingSchedule).showAndWait().ifPresent(schedule -> {
                this.operatingSchedule = schedule;
            });
        });

        // Pricing Rules Section
        Label pricingLabel = new Label("Dynamic Pricing Rules");
        pricingLabel.setStyle("-fx-font-weight: bold");

        HBox pricingButtons = new HBox(10);
        Button addRuleBtn = new Button("Add Rule");
        addRuleBtn.setOnAction(e -> {
            new PricingRuleDialog(null).showAndWait().ifPresent(rule -> {
                pricingRules.add(rule);
                updatePricingRulesList();
            });
        });

        Button removeRuleBtn = new Button("Remove Rule");
        removeRuleBtn.setOnAction(e -> {
            PricingRule selected = pricingRulesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                pricingRules.remove(selected);
                updatePricingRulesList();
            }
        });

        pricingButtons.getChildren().addAll(addRuleBtn, removeRuleBtn);

        advancedBox.getChildren().addAll(
                scheduleLabel, scheduleBtn,
                new Separator(),
                pricingLabel, pricingRulesList, pricingButtons);
        advancedTab.setContent(advancedBox);

        tabPane.getTabs().addAll(generalTab, advancedTab);
        getDialogPane().setContent(tabPane);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Enable/disable save button based on validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume();
            }
        });

        // Populate fields if editing
        if (restaurant != null) {
            nameField.setText(restaurant.getName());
            addressField.setText(restaurant.getAddress());
            descriptionArea.setText(restaurant.getDescription());
            if (restaurant.getRating() != null) {
                ratingLabel.setText(String.format("%.1f / 5.0 (from reviews)", restaurant.getRating()));
            } else {
                ratingLabel.setText("No reviews yet");
            }
            if (restaurant.getOwner() != null) {
                // Find matching owner from loaded list by ID to avoid Hibernate proxy issues
                ownerComboBox.getItems().stream()
                        .filter(owner -> owner.getId().equals(restaurant.getOwner().getId()))
                        .findFirst()
                        .ifPresent(ownerComboBox.getSelectionModel()::select);
            }
        } else {
            // For new restaurant, auto-select current user if they're an owner
            if (Session.getInstance().getCurrentUser() instanceof RestaurantOwner currentOwner) {
                ownerComboBox.getItems().stream()
                        .filter(owner -> owner.getId().equals(currentOwner.getId()))
                        .findFirst()
                        .ifPresent(ownerComboBox.getSelectionModel()::select);
            }
        }

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createRestaurant();
            }
            return null;
        });
    }

    private void updatePricingRulesList() {
        pricingRulesList.setItems(FXCollections.observableArrayList(pricingRules));
        pricingRulesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PricingRule rule, boolean empty) {
                super.updateItem(rule, empty);
                if (empty || rule == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%s-%s) x%.2f",
                            rule.getName(),
                            rule.getTimeRange().getStartTime(),
                            rule.getTimeRange().getEndTime(),
                            rule.getPriceModifier()));
                }
            }
        });
    }

    private void loadOwners() {
        try {
            List<RestaurantOwner> owners = ownerRepo.findAll();
            ownerComboBox.setItems(FXCollections.observableArrayList(owners));
            ownerComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(RestaurantOwner owner, boolean empty) {
                    super.updateItem(owner, empty);
                    setText(empty || owner == null ? null : owner.getUsername() + " (" + owner.getEmail() + ")");
                }
            });
            ownerComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(RestaurantOwner owner, boolean empty) {
                    super.updateItem(owner, empty);
                    setText(empty || owner == null ? null : owner.getUsername());
                }
            });
        } catch (Exception e) {
            showError("Failed to load restaurant owners: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Validate name
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showError("Restaurant name is required");
            return false;
        }

        // Validate address
        if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
            showError("Address is required");
            return false;
        }

        // Rating is now calculated from reviews, no validation needed

        // Validate owner
        if (ownerComboBox.getValue() == null) {
            showError("Owner is required");
            return false;
        }

        return true;
    }

    private Restaurant createRestaurant() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String description = descriptionArea.getText();
        // Rating is now calculated from reviews, not set manually

        RestaurantOwner owner = ownerComboBox.getValue();

        if (existingRestaurant != null) {
            // Update existing
            existingRestaurant.setName(name);
            existingRestaurant.setAddress(address);
            existingRestaurant.setDescription(description);
            // Rating is calculated from reviews, don't overwrite it
            existingRestaurant.setOwner(owner);
            existingRestaurant.setOperatingHours(operatingSchedule);

            // Update pricing rules
            // Clear existing and add new ones to maintain the collection reference if
            // possible,
            // but since we don't have clear() on the list exposed, we might need to handle
            // it differently.
            // However, we passed the list to the constructor, so we can just rely on the
            // fact that we modified the list in the dialog?
            // No, existingRestaurant.getPricingRules() returns unmodifiable list.
            // We need to use add/remove methods or reflection if we want to be hacky, but
            // let's use the proper methods.

            // Actually, since we can't easily clear the list via public API, let's assume
            // for now we just replace the list via reflection or add a setter in Restaurant
            // if needed.
            // But wait, Restaurant has:
            // private List<PricingRule> pricingRules = new ArrayList<>();
            // and NO setter for it.
            // It has addPricingRule and removePricingRule.

            // Let's try to sync them.
            List<PricingRule> currentRules = new ArrayList<>(existingRestaurant.getPricingRules());
            for (PricingRule rule : currentRules) {
                existingRestaurant.removePricingRule(rule);
            }
            for (PricingRule rule : pricingRules) {
                existingRestaurant.addPricingRule(rule);
            }

            return existingRestaurant;
        } else {
            // Create new - rating starts as null, will be calculated from reviews
            return new Restaurant(
                    UUID.randomUUID(),
                    name,
                    address,
                    description,
                    null, // rating calculated from reviews
                    operatingSchedule,
                    null, // menu
                    owner,
                    pricingRules);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<Restaurant> showAddDialog() {
        RestaurantDialog dialog = new RestaurantDialog(null);
        return dialog.showAndWait();
    }

    public static Optional<Restaurant> showEditDialog(Restaurant restaurant) {
        RestaurantDialog dialog;
        if (restaurant != null && restaurant.getId() != null) {
            CustomHibernate customHibernate = new CustomHibernate();
            Restaurant reloaded = customHibernate.findRestaurantWithPricingRules(restaurant.getId());
            dialog = new RestaurantDialog(reloaded != null ? reloaded : restaurant);
        } else {
            dialog = new RestaurantDialog(restaurant);
        }
        return dialog.showAndWait();
    }
}
