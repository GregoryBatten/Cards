package io.github.gregorybatten;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import io.github.gregorybatten.model.User;
import io.github.gregorybatten.service.AuthService;
import io.github.gregorybatten.service.MySQLConnector;
import io.github.gregorybatten.service.SettingService;
import io.github.gregorybatten.util.Logger;


public class App extends Application {

    private static MySQLConnector connector;
    private static AuthService authService;
    private static User user;
    private static Scene scene;

    // Get the application-wide MySQL Connector
    public static MySQLConnector getConnector() {
        return connector;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    // Get the application-wide user
    public static User getUser() {
        return user;
    }

    // Set the application-wide user
    public static void setUser(User user) {
        App.user = user; 
    }

    // Define the startup procedure - create window frame
    @Override
    public void start(Stage stage) {
        scene = new Scene(loadFXML("Login"), Integer.parseInt(SettingService.getSetting("resolutionWidth")), Integer.parseInt(SettingService.getSetting("resolutionHeight")));
        stage.setScene(scene);
        stage.show();
    }

    // Define the shutdown procedure - logout and close connection
    @Override
    public void stop() {
        if (user != null && user.getSessionEventID() != -1) {
            authService.logout();
        }
        connector.closeConnection();
    }

    // Switch scenes and log user activity
    public static void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));
        
        // If the user exists, end their sessionevent and backup data
        if (user != null) {
            getConnector().endSession(user.getSessionEventID());
            getConnector().updateUser(user);
            user.setSessionEventID(getConnector().startSession(user.getSessionID(), user.getUserID(), fxml));
        }
    }

    private static Parent loadFXML(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        try {
            return fxmlLoader.load();

        } catch (IOException e) {
            Logger.log(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        connector = new MySQLConnector();
        authService = new AuthService(connector);
        launch();
    }
}