package ie.bask.models;

import android.support.annotation.NonNull;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private String county;

    public User() {

    }

    public User(String id, String email, String username, String password, String county) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.county = county;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", county='" + county + '\'' +
                '}';
    }
}
