package com.expensetracker.model;

import java.io.Serializable;
import java.time.LocalDateTime;


public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Role { STAFF, ADMIN }
    
    private int id;
    private String username;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    
    // Default constructor for JDBC
    public User() {}
    
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}