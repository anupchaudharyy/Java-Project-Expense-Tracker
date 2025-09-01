package com.expensetracker.dao;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Expense operations
 * Demonstrates: Complex JDBC operations, JOIN queries, aggregation
 */
public class ExpenseDAO {
    private DatabaseManager dbManager;
    
    public ExpenseDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create new expense
     * Demonstrates: INSERT with BigDecimal, Date handling
     */
    public boolean createExpense(Expense expense) throws DatabaseException {
        String sql = "INSERT INTO expenses (user_id, category_id, amount, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, expense.getUserId());
            stmt.setInt(2, expense.getCategoryId());
            stmt.setBigDecimal(3, expense.getAmount());
            stmt.setString(4, expense.getDescription());
            stmt.setDate(5, Date.valueOf(expense.getExpenseDate()));
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create expense", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Get expenses with category information
     * Demonstrates: JOIN queries, complex result mapping
     */
    public List<Expense> getExpensesByUserId(int userId) throws DatabaseException {
        String sql = "SELECT e.*, c.name as category_name FROM expenses e " +
                    "JOIN expense_categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? ORDER BY e.expense_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Expense> expenses = new ArrayList<>();
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setCategoryId(rs.getInt("category_id"));
                expense.setAmount(rs.getBigDecimal("amount"));
                expense.setDescription(rs.getString("description"));
                expense.setExpenseDate(rs.getDate("expense_date").toLocalDate());
                expense.setCategoryName(rs.getString("category_name"));
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve expenses", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return expenses;
    }
    
    /**
     * Get monthly expense summary
     * Demonstrates: Aggregation queries, HashMap for key-value pairs
     */
    public Map<String, BigDecimal> getMonthlySummary(int userId, int year, int month) throws DatabaseException {
        String sql = "SELECT c.name, SUM(e.amount) as total FROM expenses e " +
                    "JOIN expense_categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? AND YEAR(e.expense_date) = ? AND MONTH(e.expense_date) = ? " +
                    "GROUP BY c.id, c.name";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, BigDecimal> summary = new HashMap<>();
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                summary.put(rs.getString("name"), rs.getBigDecimal("total"));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get monthly summary", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return summary;
    }
    
    /**
     * Update expense
     * Demonstrates: UPDATE operations
     */
    public boolean updateExpense(Expense expense) throws DatabaseException {
        String sql = "UPDATE expenses SET category_id = ?, amount = ?, description = ?, expense_date = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, expense.getCategoryId());
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setString(3, expense.getDescription());
            stmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            stmt.setInt(5, expense.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update expense", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete expense
     * Demonstrates: DELETE operations
     */
    public boolean deleteExpense(int expenseId) throws DatabaseException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, expenseId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete expense", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Get all categories
     * Demonstrates: Simple SELECT, ArrayList usage
     */
    public List<ExpenseCategory> getAllCategories() throws DatabaseException {
        String sql = "SELECT * FROM expense_categories ORDER BY name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ExpenseCategory> categories = new ArrayList<>();
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ExpenseCategory category = new ExpenseCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve categories", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return categories;
    }
}