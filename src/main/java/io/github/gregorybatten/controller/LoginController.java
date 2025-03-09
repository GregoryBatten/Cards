package io.github.gregorybatten.controller;

import io.github.gregorybatten.App;
import io.github.gregorybatten.ui.Popup;
import io.github.gregorybatten.util.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;
    private String username;

    @FXML
    private PasswordField passwordField;
    private String password;

    @FXML
    private void switchToChangePassword() {
        App.setRoot("Change Password");
    }

    @FXML
    private void switchToCreateAccount() {
        App.setRoot("Create Account");
    }

    @FXML
    private void handleLogin() {
        username = usernameField.getText();
        password = passwordField.getText();

        // Check if input is valid and attempt login
        if (!username.isEmpty() && !password.isEmpty()) {

            // Handle successful login
            if (App.getAuthService().login(username, password)) {
                Popup.showInfo("Login successful", "Welcome back, " + username);
                Logger.log("Login successful: Welcome back, " + username);
                App.setRoot("Main Menu");

            // Handle invalid username or password
            } else {
                Popup.showError("Login failed", "Incorrect username or password.");
                Logger.log("Login failed: Incorrect username or password.");
            }

        // Handle invalid input
        } else {            
            Popup.showError("Login failed", "Please fill out all fields.");
            Logger.log("Login failed: Please fill out all fields.");
        }
    }
}
