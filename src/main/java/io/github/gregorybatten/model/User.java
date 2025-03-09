package io.github.gregorybatten.model;
import java.sql.Timestamp;



public class User {
    int userid;
    int sessionid;
    int sessioneventid;
    String username;
    String password;
    String email;
    Timestamp dateJoined;
    int currency;

    public User(User user) {
        this.userid = user.getUserID();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.dateJoined = user.getDateJoined();
        this.currency = user.getCurrency();
    }

    public User(int userid, String username, String password, String email, Timestamp dateJoined, int currency) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateJoined = dateJoined;
        this.currency = currency;
    }

    public int getUserID() {
        return userid;
    }

    public int getSessionID() {
        return sessionid;
    }

    public void setSessionID(int sessionid) {
        this.sessionid = sessionid;
    }

    public int getSessionEventID() {
        return sessioneventid;
    }

    public void setSessionEventID(int sessioneventid) {
        this.sessioneventid = sessioneventid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Timestamp dateJoined) {
        this.dateJoined = dateJoined;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrecy(int currency) {
        this.currency = currency;
    }
}
