package com.expensetracker.model;

import java.io.Serializable;


public class ExpenseCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String description;
    
    public ExpenseCategory() {}
    
    public ExpenseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return name; 
    }
}