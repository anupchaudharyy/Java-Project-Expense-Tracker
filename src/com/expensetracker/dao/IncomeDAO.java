package com.expensetracker.dao;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.model.Income;
import com.expensetracker.model.IncomeCategory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeDAO {
    private DatabaseManager dbManager;

    public IncomeDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean createIncome(Income income) throws DatabaseException {
        String sql = "INSERT INTO incomes (user_id, category_id, amount, description, income_date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, income.getUserId());
            stmt.setInt(2, income.getCategoryId());
            stmt.setBigDecimal(3, income.getAmount());
            stmt.setString(4, income.getDescription());
            stmt.setDate(5, Date.valueOf(income.getIncomeDate()));

            int result = stmt.executeUpdate();

            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    income.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to create income", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }

    public List<Income> getIncomesByUserId(int userId) throws DatabaseException {
        String sql = "SELECT i.*, c.name as category_name FROM incomes i " +
                     "JOIN income_categories c ON i.category_id = c.id " +
                     "WHERE i.user_id = ? ORDER BY i.income_date DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Income> incomes = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Income income = new Income();
                income.setId(rs.getInt("id"));
                income.setUserId(rs.getInt("user_id"));
                income.setCategoryId(rs.getInt("category_id"));
                income.setAmount(rs.getBigDecimal("amount"));
                income.setDescription(rs.getString("description"));
                income.setIncomeDate(rs.getDate("income_date").toLocalDate());
                income.setCategoryName(rs.getString("category_name"));
                incomes.add(income);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve incomes", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }

        return incomes;
    }

    public Map<String, BigDecimal> getMonthlySummary(int userId, int year, int month) throws DatabaseException {
        String sql = "SELECT c.name, SUM(i.amount) as total FROM incomes i " +
                     "JOIN income_categories c ON i.category_id = c.id " +
                     "WHERE i.user_id = ? AND YEAR(i.income_date) = ? AND MONTH(i.income_date) = ? " +
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
            throw new DatabaseException("Failed to get monthly income summary", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }

        return summary;
    }

    public boolean updateIncome(Income income) throws DatabaseException {
        String sql = "UPDATE incomes SET category_id = ?, amount = ?, description = ?, income_date = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, income.getCategoryId());
            stmt.setBigDecimal(2, income.getAmount());
            stmt.setString(3, income.getDescription());
            stmt.setDate(4, Date.valueOf(income.getIncomeDate()));
            stmt.setInt(5, income.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update income", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }

    public boolean deleteIncome(int incomeId) throws DatabaseException {
        String sql = "DELETE FROM incomes WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, incomeId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete income", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }

    public List<IncomeCategory> getAllCategories() throws DatabaseException {
        String sql = "SELECT * FROM income_categories ORDER BY name";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<IncomeCategory> categories = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                IncomeCategory category = new IncomeCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve income categories", e);
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }

        return categories;
    }
}