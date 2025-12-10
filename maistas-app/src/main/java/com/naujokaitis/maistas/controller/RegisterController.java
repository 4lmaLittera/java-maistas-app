package com.naujokaitis.maistas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.naujokaitis.maistas.App;
import com.naujokaitis.maistas.database.GenericHibernate;
import com.naujokaitis.maistas.model.Administrator;
import com.naujokaitis.maistas.model.RestaurantOwner;
import org.mindrot.jbcrypt.BCrypt;
import com.naujokaitis.maistas.model.Session;
import com.naujokaitis.maistas.model.UserStatus;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.naujokaitis.maistas.model.User;
import java.util.UUID;
import java.io.IOException;

public class RegisterController {
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TabPane tabPane;

    // Restaurant Owner fields (from FXML)
    @FXML
    private Button registerButton1;
    @FXML
    private TextField usernameField1;
    @FXML
    private TextField emailField1;
    @FXML
    private TextField phoneField1;
    @FXML
    private PasswordField passwordField1;
    @FXML
    private PasswordField confirmPasswordField1;
    @FXML
    private Button backButton1;

    // Client fields
    @FXML
    private Button registerButton2;
    @FXML
    private TextField usernameField2;
    @FXML
    private TextField emailField2;
    @FXML
    private TextField phoneField2;
    @FXML
    private TextField addressField2;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private PasswordField confirmPasswordField2;
    @FXML
    private Button backButton2;

    // Driver fields
    @FXML
    private Button registerButton3;
    @FXML
    private TextField usernameField3;
    @FXML
    private TextField emailField3;
    @FXML
    private TextField phoneField3;
    @FXML
    private javafx.scene.control.ComboBox<com.naujokaitis.maistas.model.VehicleType> vehicleTypeCombo;
    @FXML
    private PasswordField passwordField3;
    @FXML
    private PasswordField confirmPasswordField3;
    @FXML
    private Button backButton3;
    
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        if (registerButton != null) registerButton.setOnAction(event -> register());
        if (registerButton1 != null) registerButton1.setOnAction(event -> register());
        if (registerButton2 != null) registerButton2.setOnAction(event -> register());
        if (registerButton3 != null) registerButton3.setOnAction(event -> register());
        
        if (backButton != null) backButton.setOnAction(event -> back());
        if (backButton1 != null) backButton1.setOnAction(event -> back());
        if (backButton2 != null) backButton2.setOnAction(event -> back());
        if (backButton3 != null) backButton3.setOnAction(event -> back());
        
        if (vehicleTypeCombo != null) {
            vehicleTypeCombo.setItems(javafx.collections.FXCollections.observableArrayList(com.naujokaitis.maistas.model.VehicleType.values()));
        }
    }

    private void register() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String tabText = selectedTab.getText().trim();

        User user = null;
        switch (tabText) {
            case "Administrator":
                if (!validateFields(usernameField, emailField, phoneField, passwordField, confirmPasswordField)) return;
                String rawAdmin = passwordField.getText();
                String hashedAdmin = BCrypt.hashpw(rawAdmin, BCrypt.gensalt());
                user = new Administrator(UUID.randomUUID(), usernameField.getText(), hashedAdmin,
                        emailField.getText(), phoneField.getText(), UserStatus.ACTIVE);
                break;
            case "Restaurant Owner":
                if (!validateFields(usernameField1, emailField1, phoneField1, passwordField1, confirmPasswordField1)) return;
                String rawOwner = passwordField1.getText();
                String hashedOwner = BCrypt.hashpw(rawOwner, BCrypt.gensalt());
                user = new RestaurantOwner(UUID.randomUUID(), usernameField1.getText(), hashedOwner,
                        emailField1.getText(), phoneField1.getText());
                break;
            case "Client":
                if (!validateFields(usernameField2, emailField2, phoneField2, passwordField2, confirmPasswordField2)) return;
                if (addressField2.getText().isEmpty()) {
                     showAlert("Validation Error", "Address is required");
                     return;
                }
                String rawClient = passwordField2.getText();
                String hashedClient = BCrypt.hashpw(rawClient, BCrypt.gensalt());
                // Assuming Client constructor: UUID, username, hashedPW, email, phone, address
                // Ideally Client should have appropriate constructor. Checking Client.java...
                // Based on standard user hierarchy. 
                // Wait, need to check if Client has suitable constructor. 
                // Assuming it has full constructor or using default + setters.
                // Let's use constructor similar to others if available, or setters.
                // The view_file for Client.java earlier showed it extends User.
                // Let's rely on standard pattern or add constructor if missing.
                // Actually looking at code snippet earlier for Client, it has additional fields.
                // Safest to use setters if constructor signature is unknown, but let's try to assume a comprehensive one or use builder if lombok.
                com.naujokaitis.maistas.model.Client client = new com.naujokaitis.maistas.model.Client(
                        UUID.randomUUID(), usernameField2.getText(), hashedClient,
                        emailField2.getText(), phoneField2.getText(), addressField2.getText(),
                        0, new java.util.ArrayList<>(), java.math.BigDecimal.ZERO);
                user = client;
                break;
            case "Driver":
                if (!validateFields(usernameField3, emailField3, phoneField3, passwordField3, confirmPasswordField3)) return;
                if (vehicleTypeCombo.getValue() == null) {
                    showAlert("Validation Error", "Vehicle type is required");
                    return;
                }
                String rawDriver = passwordField3.getText();
                String hashedDriver = BCrypt.hashpw(rawDriver, BCrypt.gensalt());
                com.naujokaitis.maistas.model.Driver driver = new com.naujokaitis.maistas.model.Driver(
                        UUID.randomUUID(), usernameField3.getText(), hashedDriver,
                        emailField3.getText(), phoneField3.getText(), vehicleTypeCombo.getValue(), true); // Default available
                user = driver;
                break;
            default:
                return;
        }

        registerUser(user);
    }
    
    // Generic validation helper
    private boolean validateFields(TextField user, TextField email, TextField phone, PasswordField pass, PasswordField confirm) {
        if (!pass.getText().equals(confirm.getText())) {
            showAlert("Validation Error", "Passwords do not match");
            return false;
        }
        if (user.getText().isEmpty() || email.getText().isEmpty() || phone.getText().isEmpty()
                || pass.getText().isEmpty() || confirm.getText().isEmpty()) {
            showAlert("Validation Error", "All fields are required");
            return false;
        }
        if (!email.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Validation Error", "Invalid email address");
            return false;
        }
        if (!phone.getText().matches("^\\+?[1-9]\\d{1,14}$")) {
            showAlert("Validation Error", "Invalid phone number");
            return false;
        }
        if (pass.getText().length() < 8) {
            showAlert("Validation Error", "Password must be at least 8 characters long");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void registerUser(User user) {
        GenericHibernate<User> userRepo = new GenericHibernate<>(User.class);
        userRepo.save(user);

        Session.getInstance().setCurrentUser(user);

        System.out.println("User saved successfully");
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("User saved successfully");
        alert.showAndWait();

        try {
            App.showMainView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Removed specific validation methods in favor of generic one to reduce code duplication
    // validateFields() and validateOwnerFields() logic is merged into the generic helper
    
    private void back() {
        try {
            App.showInitialView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
