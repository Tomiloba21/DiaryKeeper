package dev.sec3.se3.model;

public class User extends BaseEntity{
    private String username;
    private String passwordHash;
    private String email;
    private UserRole role;

    public User(String username, String passwordHash, String email) {
        super();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = UserRole.USER;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
