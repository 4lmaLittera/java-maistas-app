package com.naujokaitis.maistas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.naujokaitis.maistas.database.DatabaseConnection;
import com.naujokaitis.maistas.dao.UserDAO;
import com.naujokaitis.maistas.App;
import com.naujokaitis.maistas.model.Administrator;
import com.naujokaitis.maistas.model.RestaurantOwner;
import com.naujokaitis.maistas.model.Session;
import com.naujokaitis.maistas.model.UserStatus;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.SQLException;
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
    private void initialize() {
        registerButton.setOnAction(event -> register());
        if (registerButton1 != null) {
            registerButton1.setOnAction(event -> register());
        }
    }

    private void register() {
        if (!validateFields()) {
            return;
        }

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String tabText = selectedTab.getText().trim();

        User user = null;
        switch (tabText) {
            case "Administrator":
                user = new Administrator(UUID.randomUUID(), usernameField.getText(), passwordField.getText(),
                        emailField.getText(), phoneField.getText(), UserStatus.ACTIVE);
                break;
            case "Restaurant Owner":
                // validate owner fields separately
                if (!validateOwnerFields()) {
                    return;
                }
                user = new RestaurantOwner(UUID.randomUUID(), usernameField1.getText(), passwordField1.getText(),
                        emailField1.getText(), phoneField1.getText());
                break;
        }

        registerUser(user);
    }

    private void registerUser(User user) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            UserDAO userDAO = new UserDAO(connection);

            boolean success = userDAO.saveUserToDatabase(user);
            if (success) {
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
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save user");
                alert.showAndWait();
            }
            DatabaseConnection.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            System.out.println("Passwords do not match");
            return false;
        }
        if (usernameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty()
                || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            System.out.println("All fields are required");
            return false;
        }
        if (!emailField.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email address");
            return false;
        }
        if (!phoneField.getText().matches("^\\+?[1-9]\\d{1,14}$")) {
            System.out.println("Invalid phone number");
            return false;
        }
        if (passwordField.getText().length() < 8) {
            System.out.println("Password must be at least 8 characters long");
            return false;
        }
        if (usernameField.getText().length() > 50) {
            System.out.println("Username must be less than 50 characters");
            return false;
        }
        if (emailField.getText().length() > 255) {
            System.out.println("Email must be less than 255 characters");
            return false;
        }
        if (phoneField.getText().length() > 15) {
            System.out.println("Phone number must be less than 15 characters");
            return false;
        }
        if (passwordField.getText().length() > 255) {
            System.out.println("Password must be less than 255 characters");
            return false;
        }
        return true;
    }

    private boolean validateOwnerFields() {
        if (passwordField1 == null || confirmPasswordField1 == null) {
            System.out.println("Password fields missing");
            return false;
        }
        if (!passwordField1.getText().equals(confirmPasswordField1.getText())) {
            System.out.println("Passwords do not match");
            return false;
        }
        if (usernameField1.getText().isEmpty() || emailField1.getText().isEmpty() || phoneField1.getText().isEmpty()
                || passwordField1.getText().isEmpty() || confirmPasswordField1.getText().isEmpty()) {
            System.out.println("All fields are required");
            return false;
        }
        if (!emailField1.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email address");
            return false;
        }
        if (!phoneField1.getText().matches("^\\+?[1-9]\\d{1,14}$")) {
            System.out.println("Invalid phone number");
            return false;
        }
        if (passwordField1.getText().length() < 8) {
            System.out.println("Password must be at least 8 characters long");
            return false;
        }
        if (usernameField1.getText().length() > 50) {
            System.out.println("Username must be less than 50 characters");
            return false;
        }
        if (emailField1.getText().length() > 255) {
            System.out.println("Email must be less than 255 characters");
            return false;
        }
        if (phoneField1.getText().length() > 15) {
            System.out.println("Phone number must be less than 15 characters");
            return false;
        }
        if (passwordField1.getText().length() > 255) {
            System.out.println("Password must be less than 255 characters");
            return false;
        }
        return true;
    }
}
