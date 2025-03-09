package io.github.gregorybatten.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.github.gregorybatten.App;
import io.github.gregorybatten.model.User;
import io.github.gregorybatten.util.Logger;

public class AuthService {
    private MySQLConnector db;
    private static final Map<String, String> resetCodes = new HashMap<>();
    private static final Map<String, String> accountCodes = new HashMap<>();


    public AuthService(MySQLConnector db) {
        this.db = db;
    }

    // 
    public boolean login(String username, String password) {
        User user = null;
        String ipAddress = "Unknown";

        if (isRegisteredUser(username)) {
            user = db.getUser(username);
        }

        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();

        // Handle missing IP Address
        } catch (UnknownHostException e) {
            Logger.log(e.getMessage());
        }

        if (user != null && user.getPassword().equals(password)) {
            user.setSessionID(db.getLoginCount(user.getUserID()));
            db.logLoginAttempt(username, password, ipAddress, user.getUserID(), user.getSessionID());
            App.setUser(user);
            return true;

        } else {
            db.logLoginAttempt(username, password, ipAddress, null, null);
            return false;
        }
    }
    
    public void logout() {
        if (App.getUser() != null) {
            App.getConnector().endSession(App.getUser().getSessionEventID());
            App.getConnector().updateUser(App.getUser());
            App.setUser(null);
        }
    }

    public boolean isRegisteredUser(int userid) {
        return App.getConnector().getUser(userid) != null;
    }

    public boolean isRegisteredUser(String username) {
        return App.getConnector().getUser(username) != null;
    }

    public boolean isRegisteredEmail(String email) {
        return App.getConnector().getUserByEmail(email) != null;
    }

    public boolean isValidUsername(String username) {
        return (
            username != null
            && !isRegisteredUser(username)
            && username.equals(username.strip())
            && username.length() > 5
        );
    }

    public boolean isValidEmail(String email) {
        return (
            email != null
            && !isRegisteredEmail(email)
            && email.equals(email.strip())
            && email.contains("@")
            && email.contains(".")
        );
    }

    public boolean isValidPassword(String password) {
        return (
            password != null
            && password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        );
    }

    private String generateRandomCode() {
        return "12345";
    }

    public boolean sendChangePasswordCode(String email) {
        String code = generateRandomCode();
        resetCodes.put(email, code);
        return true;
    }
        
    public boolean changePassword(int userId, String password, String code) {
        User user = db.getUser(userId);
        if (user != null && isValidPassword(password) && code.equals(resetCodes.get(user.getEmail()))) {
            resetCodes.remove(user.getEmail());
            return db.changePassword(password, userId);
        }
        return false;
    }
    
    public boolean sendCreateAccountCode(String email) {
        String code = generateRandomCode();
        accountCodes.put(email, code);
        return true;
    }

    public boolean createAccount(String username, String password, String email, String code) {
        if (isValidUsername(username) && isValidPassword(password) && isValidEmail(email) && code.equals(accountCodes.get(email))) {
            accountCodes.remove(email);
            return App.getConnector().insertUser(username, password, email) != -1;
        }
        else return false;
    }
}
