package com.naujokaitis.maistas;

import javafx.application.Application;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import com.naujokaitis.maistas.database.DatabaseConnection;
import com.naujokaitis.maistas.database.DatabaseInitializer;

import java.sql.SQLException;

public class App extends Application {
    private static Stage primaryStage;


    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            System.out.println("Connected to database");
            DatabaseConnection.closeConnection(connection);
            System.out.println("Disconnected from database");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseInitializer.initializeDatabase();

        Application.launch(args);

    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showInitialView();
        primaryStage.show();
    }

    public static void showInitialView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/InitialView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Pradinis langas");
    }

    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/LoginView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Prisijungimas");
    }

    public static void showRegisterView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/RegisterView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Registracija");
    }

    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/MainView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Pagrindinis langas");
        
    }
}
