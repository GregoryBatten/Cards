package io.github.gregorybatten.controller;

import io.github.gregorybatten.App;
import io.github.gregorybatten.model.User;
import io.github.gregorybatten.ui.Popup;
import io.github.gregorybatten.util.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ChangePasswordController {
    User user;

    @FXML
    private TextField emailField;
    private String email;

    @FXML
    private TextField codeField;
    private String code;
    
    @FXML
    private PasswordField passwordField;
    private String password;

    @FXML
    private PasswordField passwordField2;
    private String password2;

    @FXML
    private void switchToLogin() {
        App.setRoot("Login");
    }

    @FXML
    private void handleCodeEmail() {
        email = emailField.getText();
        user = App.getConnector().getUserByEmail(email);

        // Check if user exists and confirm password change
        if (user != null && Popup.showConfirmation("Do you want to change your password?", "Do you want to change your password?") 
            && App.getAuthService().sendChangePasswordCode(email)) {

            Popup.showInfo("Email sent", "The password change activation code will be in your inbox shortly. (Remeber to wait a few minutes and check spam)");
            Logger.log("Email sent: The password change activation code will be in your inbox shortly.");

        // Handle email failed to send
        } else {
            Popup.showInfo("Email failed to send", "Check the fields and try again.");
            Logger.log("Email failed to send: Check the fields and try again.");
        }
    }

    @FXML
    private void handleChangePassword() {
        code = codeField.getText();
        password = passwordField.getText();
        password2 = passwordField2.getText();

        // Check if account details are valid and handle password change
        if (!user.getPassword().equals(password)) {
            if (password.equals(password2) && App.getAuthService().changePassword(user.getUserID(), password, code)) {

                    Popup.showInfo("Password updated", "New password created for " + user.getUsername());
                    Logger.log("Password updated: New password created for " + user.getUsername());
                    switchToLogin();

            // Handle password change failed
            } else {
                Popup.showError("Password change failed", "Check the fields and try again.");
                Logger.log("Password change failed: Check the fields and try again.");
            }
        
        // Handle changing to same password
        } else {
            Popup.showError("Password change failed", "New password must be different than the old password.");
            Logger.log("Password change failed: New password must be different than the old password.");
        }
    }
}
