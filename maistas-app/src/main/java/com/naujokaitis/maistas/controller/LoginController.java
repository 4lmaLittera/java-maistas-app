package com.naujokaitis.maistas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Button loginButton;
    
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> login());
    }

    private void login() {
        System.out.println("Login button clicked");
        System.out.println("Username: " + usernameField.getText());
        System.out.println("Password: " + passwordField.getText());
    }
}
