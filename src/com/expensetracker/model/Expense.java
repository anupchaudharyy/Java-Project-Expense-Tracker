package com.expensetracker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private int categoryId;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    private String categoryName; // For display purposes
    
    public Expense() {}
    
    public Expense(int userId, int categoryId, BigDecimal amount, 
                   String description, LocalDate expenseDate) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    @Override
    public String toString() {
        return "Expense{id=" + id + ", amount=" + amount + 
               ", description='" + description + "', date=" + expenseDate + "}";
    }
}