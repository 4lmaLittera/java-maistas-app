package com.naujokaitis.maistas.controller;

import com.naujokaitis.maistas.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class UserDialog extends Dialog<User> {

    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextField emailField;
    private final TextField phoneField;
    private final ComboBox<UserRole> roleComboBox;
    private final ComboBox<UserStatus> statusComboBox;

    // Client-specific fields
    private final TextField addressField;

    // Driver-specific fields
    private final ComboBox<VehicleType> vehicleComboBox;
    private final CheckBox availableCheckBox;

    private User existingUser;

    public UserDialog(User user) {
        this.existingUser = user;

        setTitle(user == null ? "Create User" : "Edit User");
        setHeaderText(user == null ? "Create a new user" : "Edit user: " + user.getUsername());

        // Create form fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText(user == null ? "Password" : "New password (leave empty to keep current)");
        passwordField.setPrefWidth(250);

        emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefWidth(250);

        phoneField = new TextField();
        phoneField.setPromptText("+370...");
        phoneField.setPrefWidth(250);

        roleComboBox = new ComboBox<>();
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setPromptText("Select role");
        roleComboBox.setPrefWidth(250);

        statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList(UserStatus.values()));
        statusComboBox.setPromptText("Select status");
        statusComboBox.setPrefWidth(250);

        // Client fields
        addressField = new TextField();
        addressField.setPromptText("Default delivery address");
        addressField.setPrefWidth(250);

        // Driver fields
        vehicleComboBox = new ComboBox<>();
        vehicleComboBox.setItems(FXCollections.observableArrayList(VehicleType.values()));
        vehicleComboBox.setPromptText("Select vehicle type");
        vehicleComboBox.setPrefWidth(250);

        availableCheckBox = new CheckBox("Available for deliveries");

        // Pre-fill if editing
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setEditable(false); // Don't allow username change
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            roleComboBox.setValue(user.getRole());
            roleComboBox.setDisable(true); // Don't allow role change
            statusComboBox.setValue(user.getStatus());

            if (user instanceof Client client) {
                addressField.setText(client.getDefaultAddress());
            } else if (user instanceof Driver driver) {
                vehicleComboBox.setValue(driver.getVehicleType());
                availableCheckBox.setSelected(driver.isAvailable());
            }
        } else {
            statusComboBox.setValue(UserStatus.ACTIVE);
        }

        // Setup grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        int row = 0;
        grid.add(new Label("Username:*"), 0, row);
        grid.add(usernameField, 1, row++);

        grid.add(new Label("Password:" + (user == null ? "*" : "")), 0, row);
        grid.add(passwordField, 1, row++);

        grid.add(new Label("Email:*"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Phone:*"), 0, row);
        grid.add(phoneField, 1, row++);

        grid.add(new Label("Role:*"), 0, row);
        grid.add(roleComboBox, 1, row++);

        grid.add(new Label("Status:*"), 0, row);
        grid.add(statusComboBox, 1, row++);

        // Role-specific fields
        Label addressLabel = new Label("Address:");
        Label vehicleLabel = new Label("Vehicle:");

        grid.add(addressLabel, 0, row);
        grid.add(addressField, 1, row++);

        grid.add(vehicleLabel, 0, row);
        grid.add(vehicleComboBox, 1, row++);

        grid.add(new Label(""), 0, row);
        grid.add(availableCheckBox, 1, row++);

        // Initially hide role-specific fields
        addressLabel.setVisible(false);
        addressField.setVisible(false);
        vehicleLabel.setVisible(false);
        vehicleComboBox.setVisible(false);
        availableCheckBox.setVisible(false);

        // Show/hide fields based on role
        roleComboBox.setOnAction(e -> {
            UserRole selectedRole = roleComboBox.getValue();
            boolean isClient = selectedRole == UserRole.CLIENT;
            boolean isDriver = selectedRole == UserRole.DRIVER;

            addressLabel.setVisible(isClient);
            addressField.setVisible(isClient);
            vehicleLabel.setVisible(isDriver);
            vehicleComboBox.setVisible(isDriver);
            availableCheckBox.setVisible(isDriver);
        });

        // Trigger visibility update for existing user
        if (user != null) {
            roleComboBox.fireEvent(new javafx.event.ActionEvent());
        }

        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType(user == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
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
                return createOrUpdateUser();
            }
            return null;
        });
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (usernameField.getText().trim().isEmpty()) {
            errors.append("Username is required\n");
        }
        if (existingUser == null && passwordField.getText().isEmpty()) {
            errors.append("Password is required\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email is required\n");
        } else if (!emailField.getText().contains("@")) {
            errors.append("Invalid email format\n");
        }
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("Phone is required\n");
        }
        if (roleComboBox.getValue() == null) {
            errors.append("Role is required\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("Status is required\n");
        }

        // Role-specific validation
        if (roleComboBox.getValue() == UserRole.CLIENT && addressField.getText().trim().isEmpty()) {
            errors.append("Address is required for clients\n");
        }
        if (roleComboBox.getValue() == UserRole.DRIVER && vehicleComboBox.getValue() == null) {
            errors.append("Vehicle type is required for drivers\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        return true;
    }

    private User createOrUpdateUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole role = roleComboBox.getValue();
        UserStatus status = statusComboBox.getValue();

        if (existingUser != null) {
            // Update existing user
            existingUser.setStatus(status);

            if (existingUser instanceof Client client) {
                client.setDefaultAddress(addressField.getText().trim());
            } else if (existingUser instanceof Driver driver) {
                driver.setVehicleType(vehicleComboBox.getValue());
                driver.setAvailable(availableCheckBox.isSelected());
            }

            return existingUser;
        }

        // Create new user
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UUID id = UUID.randomUUID();

        return switch (role) {
            case CLIENT -> new Client(id, username, hashedPassword, email, phone,
                    addressField.getText().trim(), 0, null);
            case DRIVER -> {
                Driver driver = new Driver(id, username, hashedPassword, email, phone,
                        vehicleComboBox.getValue(), availableCheckBox.isSelected());
                yield driver;
            }
            case RESTAURANT_OWNER -> new RestaurantOwner(id, username, hashedPassword, email, phone);
            case ADMIN -> new Administrator(id, username, hashedPassword, email, phone);
        };
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<User> showCreateDialog() {
        UserDialog dialog = new UserDialog(null);
        return dialog.showAndWait();
    }

    public static Optional<User> showEditDialog(User user) {
        UserDialog dialog = new UserDialog(user);
        return dialog.showAndWait();
    }
}
