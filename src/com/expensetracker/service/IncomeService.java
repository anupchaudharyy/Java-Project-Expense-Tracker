package com.expensetracker.service;

import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.model.Income;
import com.expensetracker.model.IncomeCategory;
import com.expensetracker.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class IncomeService {
    private IncomeDAO incomeDAO;

    public IncomeService() {
        this.incomeDAO = new IncomeDAO();
    }

    public boolean createIncome(int userId, int categoryId, BigDecimal amount,
                               String description, LocalDate date) throws ValidationException, DatabaseException {
        // Validate inputs
        validateIncome(amount, description, date);

        Income income = new Income(userId, categoryId, amount, description, date);
        return incomeDAO.createIncome(income);
    }

    private void validateIncome(BigDecimal amount, String description, LocalDate date) throws ValidationException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }

        if (date == null || date.isAfter(LocalDate.now())) {
            throw new ValidationException("Date cannot be in the future");
        }
        
        
    }

    public List<Income> getUserIncomes(int userId) throws DatabaseException {
        return incomeDAO.getIncomesByUserId(userId);
    }

    public List<Income> getIncomesForUser(User user) throws DatabaseException {
        return incomeDAO.getIncomesByUserId(user.getId());
    }

    public Map<String, BigDecimal> getMonthlySummary(int userId, int year, int month) throws DatabaseException {
        return incomeDAO.getMonthlySummary(userId, year, month);
    }

    public List<IncomeCategory> getAllCategories() throws DatabaseException {
        return incomeDAO.getAllCategories();
    }

    public boolean updateIncome(Income income) throws ValidationException, DatabaseException {
        validateIncome(income.getAmount(), income.getDescription(), income.getIncomeDate());
        return incomeDAO.updateIncome(income);
    }

    public boolean deleteIncome(int incomeId) throws DatabaseException {
        return incomeDAO.deleteIncome(incomeId);
    }
}