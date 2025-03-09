package io.github.gregorybatten.service;

import java.sql.*;
import java.util.function.Function;
import io.github.gregorybatten.model.User;
import io.github.gregorybatten.util.Config;
import io.github.gregorybatten.util.Logger;

public class MySQLConnector {

    private Connection connection;

    // Establish database connection
    public MySQLConnector() {
        try {
            connection = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
        } catch (SQLException e) {
            Logger.log(e.getMessage());
        }
    }

    // Get active connection
    public Connection getConnection() {
        return connection;
    }

    // Close active connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.log(e.getMessage());
        }
    }

    // Insert new user and return generated userid
    public int insertUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email, datejoined) VALUES (?, ?, ?, NOW())";
        return insert(sql, username, password, email);
    }

    // Retrieve user by userid
    public User getUser(int userid) {
        return get("SELECT userid, username, password, email, datejoined, currency FROM users WHERE userid = ?", 
                   this::mapUser, userid);
    }

    // Retrieve user by username
    public User getUser(String username) {
        return get("SELECT userid, username, password, email, datejoined, currency FROM users WHERE username = ?", 
                   this::mapUser, username);
    }

    // Retrieve user by email
    public User getUserByEmail(String email) {
        return get("SELECT userid, username, password, email, datejoined, currency FROM users WHERE email = ?", 
                   this::mapUser, email);
    }

    // Update user details
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, currency = ? WHERE userid = ?";
        return update(sql, user.getUsername(), user.getPassword(), user.getEmail(), user.getCurrency(), user.getUserID());
    }

    // Delete user by userid
    public boolean deleteUser(int userid) {
        return update("DELETE FROM users WHERE userid = ?", userid);
    }

    // Change user password
    public boolean changePassword(String password, int userid) {
        return update("UPDATE users SET password = ? WHERE userid = ?", password, userid);
    }

    // Log login attempt
    public void logLoginAttempt(String username, String password, String ipAddress, Integer userId, Integer sessionid) {
        insert("INSERT INTO login_attempts (username, password, ipaddress, date, userid, sessionid) VALUES (?, ?, ?, NOW(), ?, ?)",
               username, password, ipAddress, userId, sessionid);
    }
    
    public int getLoginCount(int userId) {
        String sql = "SELECT COUNT(*) FROM login_attempts WHERE userid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) + 1 : 1; // Increment for next session

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
            return -1;
        }
    }
    

    // Start user sessionevent and return generated sessioneventid
    public int startSession(int sessionid, int userid, String activity) {
        String sql = "INSERT INTO sessions (sessionid, userid, activity, login) VALUES (?, ?, ?, NOW())";
        return insert(sql, sessionid, userid, activity);
    }
    
    // End user session and return session duration
    public boolean endSession(int sessioneventid) {
        String sqlUpdate = "UPDATE sessions SET logout = NOW() WHERE sessioneventid = ?";
        return update(sqlUpdate, sessioneventid);
    }

    // Generic method for INSERT statements
    private int insert(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, params);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
            return -1;
        }
    }

    // Generic method for UPDATE/DELETE statements
    private boolean update(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate() > 0;

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
            return false;
        }
    }

    // Generic method for SELECT statements
    private <T> T get(String sql, Function<ResultSet, T> mapper, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapper.apply(rs) : null;

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
            return null;
        }
    }

    // Maps a ResultSet row to a User object
    private User mapUser(ResultSet rs) {
        try {
            return new User(
                rs.getInt("userid"), 
                rs.getString("username"), 
                rs.getString("password"), 
                rs.getString("email"), 
                rs.getTimestamp("datejoined"), 
                rs.getInt("currency")
            );

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
            return null;

        }
    }

    // Utility method to bind parameters to a PreparedStatement
    private void setParameters(PreparedStatement stmt, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

        // Handle database error
        } catch (SQLException e) {
            Logger.log(e.getMessage());
        }
    }
}
