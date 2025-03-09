package io.github.gregorybatten.controller;

import java.io.IOException;
import java.sql.SQLException;

import io.github.gregorybatten.App;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void switchToLogin() throws IOException, SQLException {
        App.getAuthService().logout();
        App.setRoot("Login");
    }
}