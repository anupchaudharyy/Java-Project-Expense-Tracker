package com.expensetracker.service;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import com.expensetracker.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class ExpenseService {
    private ExpenseDAO expenseDAO;
    
    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
    }
    

    public boolean createExpense(int userId, int categoryId, BigDecimal amount, 
                               String description, LocalDate date) throws ValidationException, DatabaseException {
        // Validate inputs
        validateExpense(amount, description, date);
        
        Expense expense = new Expense(userId, categoryId, amount, description, date);
        return expenseDAO.createExpense(expense);
    }
    

    private void validateExpense(BigDecimal amount, String description, LocalDate date) throws ValidationException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
        
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new ValidationException("Date cannot be in the future");
        }
        
        
        if (amount.compareTo(new BigDecimal("10000.00")) > 0) {
            throw new ValidationException("Expense amount cannot exceed $10,000");
        }
    }
    

    public List<Expense> getUserExpenses(int userId) throws DatabaseException {
        return expenseDAO.getExpensesByUserId(userId);
    }

    public List<Expense> getExpensesForUser(User user) throws DatabaseException {
        return expenseDAO.getExpensesByUserId(user.getId());
    }

    public Map<String, BigDecimal> getMonthlySummary(int userId, int year, int month) throws DatabaseException {
        return expenseDAO.getMonthlySummary(userId, year, month);
    }
    

    public List<ExpenseCategory> getAllCategories() throws DatabaseException {
        return expenseDAO.getAllCategories();
    }
    
  
    public boolean updateExpense(Expense expense) throws ValidationException, DatabaseException {
        validateExpense(expense.getAmount(), expense.getDescription(), expense.getExpenseDate());
        return expenseDAO.updateExpense(expense);
    }
    
    public boolean deleteExpense(int expenseId) throws DatabaseException {
        return expenseDAO.deleteExpense(expenseId);
    }
}