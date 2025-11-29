package com.naujokaitis.maistas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.naujokaitis.maistas.database.CustomHibernate;
import com.naujokaitis.maistas.model.User;
import com.naujokaitis.maistas.model.Session;
import com.naujokaitis.maistas.App;

import java.io.IOException;

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
        String username = usernameField.getText();
        String raw = passwordField.getText();
        CustomHibernate customHibernate = new CustomHibernate();
        User user = customHibernate.findUserByUsername(username);

        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login failed");
            alert.setHeaderText(null);
            alert.setContentText("User not found");
            alert.showAndWait();
            return;
        }

        boolean ok = user.authenticate(raw);

        if (ok) {
            Session.getInstance().setCurrentUser(user);
            try {
                App.showMainView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid password or username");
            alert.showAndWait();
        }
    }
}
