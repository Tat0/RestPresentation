package entities;

public class User {
    private long id;
    private String userName;
    private String role;
    private boolean active;

    public User() {};

    public User(long id){
        this.id = id;
    }

    public User(long id, String userName, String role, boolean active) {
        this.id = id;
        this.userName = userName;
        this.role = role;
        this.active = active;
    }

    public long getId() {
        return id;
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
}
