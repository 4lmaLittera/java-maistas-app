package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.Menu;
import com.naujokaitis.maistas.model.Restaurant;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuDialog extends Dialog<Menu> {

    private final ComboBox<Restaurant> restaurantComboBox;
    private final TextField nameField;
    private final Label infoLabel;

    private final GenericHibernate<Restaurant> restaurantRepo = new GenericHibernate<>(Restaurant.class);
    private Menu existingMenu;
    private boolean editMode;

    public MenuDialog(Menu menu, boolean editMode) {
        this.existingMenu = menu;
        this.editMode = editMode;

        setTitle(menu == null ? "Create Menu" : (editMode ? "Edit Menu" : "View Menu"));
        setHeaderText(menu == null ? "Create a new menu for a restaurant"
                : (editMode ? "Edit menu details" : "Menu Details"));

        // Create form fields
        restaurantComboBox = new ComboBox<>();
        restaurantComboBox.setPromptText("Select restaurant");

        nameField = new TextField();
        nameField.setPromptText("Menu name (e.g., Main Menu, Lunch Menu)");
        nameField.setPrefWidth(250);

        infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        infoLabel.setText("Note: Menu is automatically created with the restaurant. You can assign it here.");

        // Load restaurants only for create mode
        if (menu == null) {
            loadRestaurants();
        }

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        if (menu != null && editMode) {
            // Edit mode - editable fields
            Label idLabel = new Label("Menu ID:");
            TextField idField = new TextField(menu.getId().toString());
            idField.setEditable(false);
            grid.add(idLabel, 0, 0);
            grid.add(idField, 1, 0);

            grid.add(new Label("Name:*"), 0, 1);
            nameField.setText(menu.getName() != null ? menu.getName() : "");
            grid.add(nameField, 1, 1);

            Label itemCountLabel = new Label("Items:");
            TextField itemCountField = new TextField(String.valueOf(menu.getItems().size()));
            itemCountField.setEditable(false);
            grid.add(itemCountLabel, 0, 2);
            grid.add(itemCountField, 1, 2);
        } else if (menu != null) {
            // View mode - read only
            Label idLabel = new Label("Menu ID:");
            TextField idField = new TextField(menu.getId().toString());
            idField.setEditable(false);
            grid.add(idLabel, 0, 0);
            grid.add(idField, 1, 0);

            Label nameLabel = new Label("Name:");
            TextField nameViewField = new TextField(menu.getName());
            nameViewField.setEditable(false);
            grid.add(nameLabel, 0, 1);
            grid.add(nameViewField, 1, 1);

            Label itemCountLabel = new Label("Items:");
            TextField itemCountField = new TextField(String.valueOf(menu.getItems().size()));
            itemCountField.setEditable(false);
            grid.add(itemCountLabel, 0, 2);
            grid.add(itemCountField, 1, 2);
        } else {
            // Create mode
            grid.add(new Label("Name:*"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Restaurant:*"), 0, 1);
            grid.add(restaurantComboBox, 1, 1);
            grid.add(infoLabel, 1, 2);
        }

        getDialogPane().setContent(grid);

        // Add buttons
        if (menu == null) {
            // Create mode
            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
            createButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                if (!validateCreateInput()) {
                    event.consume();
                }
            });

            setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    return createMenu();
                }
                return null;
            });
        } else if (editMode) {
            // Edit mode
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
            saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                if (!validateEditInput()) {
                    event.consume();
                }
            });

            setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return updateMenu();
                }
                return null;
            });
        } else {
            // View mode
            getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            setResultConverter(dialogButton -> null);
        }
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantRepo.findAll();
            // Filter restaurants that don't have a menu yet
            List<Restaurant> restaurantsWithoutMenu = restaurants.stream()
                    .filter(r -> r.getMenu() == null)
                    .toList();

            restaurantComboBox.setItems(FXCollections.observableArrayList(restaurantsWithoutMenu));
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

            if (restaurantsWithoutMenu.isEmpty()) {
                infoLabel.setText("All restaurants already have menus. Create a restaurant first.");
                infoLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 11px;");
            }
        } catch (Exception e) {
            showError("Failed to load restaurants: " + e.getMessage());
        }
    }

    private boolean validateCreateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter a menu name");
            return false;
        }
        if (restaurantComboBox.getValue() == null) {
            showError("Please select a restaurant");
            return false;
        }
        return true;
    }

    private boolean validateEditInput() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter a menu name");
            return false;
        }
        return true;
    }

    private Menu createMenu() {
        Restaurant restaurant = restaurantComboBox.getValue();
        Menu menu = new Menu(UUID.randomUUID(), nameField.getText().trim());

        // Update restaurant to reference this menu
        restaurant.setMenu(menu);

        try {
            restaurantRepo.update(restaurant);
        } catch (Exception e) {
            showError("Failed to update restaurant with menu: " + e.getMessage());
        }

        return menu;
    }

    private Menu updateMenu() {
        existingMenu.setName(nameField.getText().trim());
        return existingMenu;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<Menu> showCreateDialog() {
        MenuDialog dialog = new MenuDialog(null, false);
        return dialog.showAndWait();
    }

    public static Optional<Menu> showEditDialog(Menu menu) {
        MenuDialog dialog = new MenuDialog(menu, true);
        return dialog.showAndWait();
    }

    public static void showViewDialog(Menu menu) {
        MenuDialog dialog = new MenuDialog(menu, false);
        dialog.showAndWait();
    }
}
