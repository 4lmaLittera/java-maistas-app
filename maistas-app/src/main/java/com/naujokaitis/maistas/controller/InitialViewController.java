package com.naujokaitis.maistas.controller;

import java.io.IOException;

import com.naujokaitis.maistas.App;
import javafx.fxml.FXML;

public class InitialViewController {

    @FXML
    private void login() {
        try {
            App.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void register() {
        try {
            App.showRegisterView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
