package io.github.gregorybatten.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class Popup {

    // Private constructor to prevent instantiation
    private Popup() {}

    // Generic method for showing an alert
    public static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Predefined method for showing information alerts
    public static void showInfo(String title, String content) {
        showAlert(title, content, Alert.AlertType.INFORMATION);
    }

    // Predefined method for showing warnings
    public static void showWarning(String title, String content) {
        showAlert(title, content, Alert.AlertType.WARNING);
    }

    // Predefined method for showing error messages
    public static void showError(String title, String content) {
        showAlert(title, content, Alert.AlertType.ERROR);
    }

    // Confirmation dialog that returns true if user clicks OK
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
