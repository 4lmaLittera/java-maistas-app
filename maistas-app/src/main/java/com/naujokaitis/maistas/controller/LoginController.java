package com.naujokaitis.maistas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.naujokaitis.maistas.database.DatabaseConnection;
import com.naujokaitis.maistas.dao.UserDAO;
import com.naujokaitis.maistas.model.User;
import com.naujokaitis.maistas.model.Session;
import com.naujokaitis.maistas.App;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
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

        try (Connection connection = DatabaseConnection.getConnection()) {
            UserDAO dao = new UserDAO(connection);
            User user = dao.loadUserByUsername(username);
            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login failed");
                alert.setHeaderText(null);
                alert.setContentText("User not found");
                alert.showAndWait();
                return;
            }

            String stored = user.getPasswordHash();
            boolean ok = false;

            if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
                ok = BCrypt.checkpw(raw, stored);
            } else {
                // legacy plaintext fallback
                ok = stored != null && stored.equals(raw);
                if (ok) {
                    // re-hash and update DB
                    String newHash = BCrypt.hashpw(raw, BCrypt.gensalt());
                    String id = dao.getUserIdByUsername(username);
                    if (id != null) {
                        dao.updatePasswordHashById(id, newHash);
                        // reload user so Session gets hashed password
                        user = dao.loadUserByUsername(username);
                    }
                }
            }

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
                alert.setContentText("Invalid credentials");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
