package com.naujokaitis.maistas;

import javafx.application.Application;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        Application.launch(args);

    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showInitialView();
        primaryStage.show();
    }

    @Override
    public void stop() {
        com.naujokaitis.maistas.database.JpaUtil.close();
    }

    public static void showInitialView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/InitialView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("WOLT ne mes BET norim būti!");
        primaryStage.centerOnScreen();
    }

    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/LoginView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Prisijungimas");
        primaryStage.centerOnScreen();
    }

    public static void showRegisterView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/RegisterView.fxml"));
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setTitle("Maistas – Registracija");
        primaryStage.centerOnScreen();
    }

    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/com/naujokaitis/maistas/views/MainView.fxml"));
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setTitle("Maistas – Pagrindinis langas");
        primaryStage.centerOnScreen();
    }
}
