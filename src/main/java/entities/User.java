package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("id")
    private long userId;
    private String userName;
    private String role;
    private boolean active;

    public User() {};

    public User(long id){
        this.userId = id;
    }

    public User(long id, String userName, String role, boolean active) {
        this.userId = id;
        this.userName = userName;
        this.role = role;
        this.active = active;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        this.userId =  userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
