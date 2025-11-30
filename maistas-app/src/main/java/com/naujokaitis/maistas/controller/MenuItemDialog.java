package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.Menu;
import com.naujokaitis.maistas.model.MenuCategory;
import com.naujokaitis.maistas.model.Restaurant;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuItemDialog extends Dialog<com.naujokaitis.maistas.model.MenuItem> {

    private final TextField nameField;
    private final TextArea descriptionArea;
    private final TextField priceField;
    private final ComboBox<MenuCategory> categoryComboBox;
    private final TextField inventoryField;
    private final ComboBox<Menu> menuComboBox;

    private final GenericHibernate<Menu> menuRepo = new GenericHibernate<>(Menu.class);
    private com.naujokaitis.maistas.model.MenuItem existingMenuItem;

    public MenuItemDialog(com.naujokaitis.maistas.model.MenuItem menuItem) {
        this.existingMenuItem = menuItem;

        setTitle(menuItem == null ? "Add Menu Item" : "Edit Menu Item");
        setHeaderText(menuItem == null ? "Enter menu item details" : "Update menu item details");

        // Create form fields
        nameField = new TextField();
        nameField.setPromptText("Item name");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setPrefRowCount(3);

        priceField = new TextField();
        priceField.setPromptText("Price (e.g., 12.99)");

        categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(FXCollections.observableArrayList(MenuCategory.values()));
        categoryComboBox.setPromptText("Select category");

        inventoryField = new TextField();
        inventoryField.setPromptText("Available quantity");
        inventoryField.setText("0");

        menuComboBox = new ComboBox<>();
        menuComboBox.setPromptText("Select menu");
        loadMenus();

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        grid.add(new Label("Price:*"), 0, 2);
        grid.add(priceField, 1, 2);

        grid.add(new Label("Category:*"), 0, 3);
        grid.add(categoryComboBox, 1, 3);

        grid.add(new Label("Inventory:"), 0, 4);
        grid.add(inventoryField, 1, 4);

        grid.add(new Label("Menu:*"), 0, 5);
        grid.add(menuComboBox, 1, 5);

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
        if (menuItem != null) {
            nameField.setText(menuItem.getName());
            descriptionArea.setText(menuItem.getDescription());
            priceField.setText(menuItem.getPrice().toString());
            categoryComboBox.getSelectionModel().select(menuItem.getCategory());
            inventoryField.setText(String.valueOf(menuItem.getInventoryCount()));
            if (menuItem.getMenu() != null) {
                menuComboBox.getSelectionModel().select(menuItem.getMenu());
            }
        }

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createMenuItem();
            }
            return null;
        });
    }

    private void loadMenus() {
        try {
            List<Menu> menus = menuRepo.findAll();
            menuComboBox.setItems(FXCollections.observableArrayList(menus));
            menuComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Menu menu, boolean empty) {
                    super.updateItem(menu, empty);
                    setText(empty || menu == null ? null : "Menu ID: " + menu.getId().toString().substring(0, 8));
                }
            });
            menuComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Menu menu, boolean empty) {
                    super.updateItem(menu, empty);
                    setText(empty || menu == null ? null : "Menu ID: " + menu.getId().toString().substring(0, 8));
                }
            });
        } catch (Exception e) {
            showError("Failed to load menus: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Validate name
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showError("Item name is required");
            return false;
        }

        // Validate price
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            showError("Price is required");
            return false;
        }

        try {
            BigDecimal price = new BigDecimal(priceField.getText());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                showError("Price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Price must be a valid number");
            return false;
        }

        // Validate category
        if (categoryComboBox.getValue() == null) {
            showError("Category is required");
            return false;
        }

        // Validate inventory
        try {
            int inventory = Integer.parseInt(inventoryField.getText());
            if (inventory < 0) {
                showError("Inventory cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Inventory must be a valid number");
            return false;
        }

        // Validate menu
        if (menuComboBox.getValue() == null) {
            showError("Menu is required");
            return false;
        }

        return true;
    }

    private com.naujokaitis.maistas.model.MenuItem createMenuItem() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText();
        BigDecimal price = new BigDecimal(priceField.getText());
        MenuCategory category = categoryComboBox.getValue();
        int inventory = Integer.parseInt(inventoryField.getText());
        Menu menu = menuComboBox.getValue();

        if (existingMenuItem != null) {
            // Update existing
            existingMenuItem.setName(name);
            existingMenuItem.setDescription(description);
            existingMenuItem.setPrice(price);
            existingMenuItem.setCategory(category);
            existingMenuItem.setInventoryCount(inventory);
            existingMenuItem.setMenu(menu);
            return existingMenuItem;
        } else {
            // Create new
            return new com.naujokaitis.maistas.model.MenuItem(
                    UUID.randomUUID(),
                    name,
                    description,
                    price,
                    category,
                    inventory,
                    menu);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<com.naujokaitis.maistas.model.MenuItem> showAddDialog() {
        MenuItemDialog dialog = new MenuItemDialog(null);
        return dialog.showAndWait();
    }

    public static Optional<com.naujokaitis.maistas.model.MenuItem> showEditDialog(
            com.naujokaitis.maistas.model.MenuItem menuItem) {
        MenuItemDialog dialog = new MenuItemDialog(menuItem);
        return dialog.showAndWait();
    }
}
