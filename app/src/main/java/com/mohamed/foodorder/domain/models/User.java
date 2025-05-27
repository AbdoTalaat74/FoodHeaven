package com.mohamed.foodorder.domain.models;

public class User {
    private String email;
    private String role;
    private String username;
    private long createdAt;

    public User() {}

    public User(String email, String role, String username, long createdAt) {
        this.email = email;
        this.role = role;
        this.username = username;
        this.createdAt = createdAt;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}