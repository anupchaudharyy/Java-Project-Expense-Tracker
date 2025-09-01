package com.expensetracker.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.expensetracker.model.User;
import com.expensetracker.exceptions.DatabaseException;

/**
 * Data Access Object for User operations
 * Demonstrates: JDBC CRUD operations, PreparedStatement
 */
public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Authenticate user login
     * Demonstrates: SQL queries, PreparedStatement security
     */
    public User authenticate(String username, String password) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Authentication failed", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Create new user
     * Demonstrates: INSERT operations, auto-generated keys
     */
    public boolean createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create user", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Get user by username
     * Demonstrates: SELECT operations, parameter binding
     */
    public User getUserByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get user by username", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Get all users
     * Demonstrates: SELECT operations, Collections (ArrayList)
     */
    public List<User> getAllUsers() throws DatabaseException {
        String sql = "SELECT * FROM users ORDER BY username";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                users.add(user);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve users", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return users;
    }
}