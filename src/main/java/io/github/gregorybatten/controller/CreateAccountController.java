package io.github.gregorybatten.controller;

import io.github.gregorybatten.App;
import io.github.gregorybatten.ui.Popup;
import io.github.gregorybatten.util.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CreateAccountController {
    @FXML
    private TextField emailField;
    private String email;

    @FXML
    private TextField usernameField;
    private String username;

    @FXML
    private PasswordField passwordField;
    private String password;

    @FXML
    private PasswordField passwordField2;
    private String password2;

    @FXML
    private TextField codeField;
    private String code;


    @FXML
    private void switchToLogin() {
        App.setRoot("Login");
    }

    @FXML
    private void handleCodeEmail() {
        email = emailField.getText();
        username = usernameField.getText();
        password = passwordField.getText();
        password2 = passwordField2.getText();
        
        // Check if input, email, and username are valid
        if(!email.isEmpty() && !username.isEmpty() && !password.isEmpty() && !password2.isEmpty()) {

                // Check if account details are vaild and send email
                if (App.getAuthService().isValidUsername(username) && App.getAuthService().isValidPassword(password) && App.getAuthService().isValidEmail(email) && password.equals(password2)
                    && Popup.showConfirmation("Is this information correct?", "Is this information correct?") && App.getAuthService().sendCreateAccountCode(email)) {

                    Popup.showInfo("Email Sent", "The account activation code will be sent to your inbox shortly. (Remeber to wait a few minutes and check spam)");
                    Logger.log("Email Sent: The account activation code will be sent to your inbox shortly.");

                // Handle invalid account details
                } else {
                    Popup.showInfo("Account creation failed", "Check the fields and try again.");
                    Logger.log("Account creation failed: Check the fields and try again.");
                }
            

        // Handle invalid input
        } else {
            Popup.showError("Account creation failed", "Please fill out all fields.");
            Logger.log("Account creation failed: Please fill out all fields.");
        }
    }
    
    @FXML
    private void handleCreateAccount() {
        code = codeField.getText();

        // Handle create account
        if (App.getAuthService().createAccount(username, password, email, code)) {
            Popup.showInfo("Account created", "New account created for " + username);
            Logger.log("Account created: New account created for " + username);
            switchToLogin();

        // Handle create account failed
        } else {
            Popup.showError("Account creation failed", "Check the fields and try again.");
            Logger.log("Account creation failed: Check the fields and try again.");
        }
    }
}
