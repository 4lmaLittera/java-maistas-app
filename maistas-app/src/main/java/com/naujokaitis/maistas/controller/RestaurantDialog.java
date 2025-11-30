package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.Restaurant;
import com.naujokaitis.maistas.model.RestaurantOwner;
import com.naujokaitis.maistas.model.Session;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RestaurantDialog extends Dialog<Restaurant> {

    private final TextField nameField;
    private final TextField addressField;
    private final TextArea descriptionArea;
    private final TextField ratingField;
    private final ComboBox<RestaurantOwner> ownerComboBox;

    private final GenericHibernate<RestaurantOwner> ownerRepo = new GenericHibernate<>(RestaurantOwner.class);
    private Restaurant existingRestaurant;

    public RestaurantDialog(Restaurant restaurant) {
        this.existingRestaurant = restaurant;

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

        ratingField = new TextField();
        ratingField.setPromptText("Rating (0-5, optional)");

        ownerComboBox = new ComboBox<>();
        ownerComboBox.setPromptText("Select owner");
        loadOwners();

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Address:*"), 0, 1);
        grid.add(addressField, 1, 1);

        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);

        grid.add(new Label("Rating:"), 0, 3);
        grid.add(ratingField, 1, 3);

        grid.add(new Label("Owner:*"), 0, 4);
        grid.add(ownerComboBox, 1, 4);

        getDialogPane().setContent(grid);

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
                ratingField.setText(restaurant.getRating().toString());
            }
            if (restaurant.getOwner() != null) {
                ownerComboBox.getSelectionModel().select(restaurant.getOwner());
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

        // Validate rating if provided
        String ratingText = ratingField.getText();
        if (ratingText != null && !ratingText.trim().isEmpty()) {
            try {
                double rating = Double.parseDouble(ratingText);
                if (rating < 0 || rating > 5) {
                    showError("Rating must be between 0 and 5");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Rating must be a valid number");
                return false;
            }
        }

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
        Double rating = null;

        String ratingText = ratingField.getText();
        if (ratingText != null && !ratingText.trim().isEmpty()) {
            rating = Double.parseDouble(ratingText);
        }

        RestaurantOwner owner = ownerComboBox.getValue();

        if (existingRestaurant != null) {
            // Update existing
            existingRestaurant.setName(name);
            existingRestaurant.setAddress(address);
            existingRestaurant.setDescription(description);
            existingRestaurant.setRating(rating);
            existingRestaurant.setOwner(owner);
            return existingRestaurant;
        } else {
            // Create new
            return new Restaurant(
                    UUID.randomUUID(),
                    name,
                    address,
                    description,
                    rating,
                    null, // operatingHours
                    null, // menu
                    owner,
                    List.of() // pricingRules
            );
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
        RestaurantDialog dialog = new RestaurantDialog(restaurant);
        return dialog.showAndWait();
    }
}
