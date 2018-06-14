package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class User {
    @JsonProperty("id")
    private long userId;
    private String userName;
    private String role;
    private boolean active;

    public User() {}

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

    public void setUserId(long userId) {
        this.userId = userId;
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
        return new StringBuilder()
                .append("User{")
                .append("userId=").append(userId)
                .append(", userName='").append(userName)
                .append(", role='").append(role)
                .append(", active=").append(active)
                .append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                active == user.active &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, role, active);
    }
}
