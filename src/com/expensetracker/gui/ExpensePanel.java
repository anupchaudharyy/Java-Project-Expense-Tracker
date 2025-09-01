package com.expensetracker.gui;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.utils.ExceptionHandler;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ExpensePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private User currentUser;
    private ExpenseService expenseService;
    
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JButton updateButton;
    private JButton deleteButton;
    
    private final Color PRIMARY_COLOR = new Color(40, 44, 52);
    private final Color SECONDARY_COLOR = new Color(33, 37, 43);
    private final Color ACCENT_COLOR = new Color(97, 218, 251);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font GLOBAL_FONT = new Font("Roboto", Font.PLAIN, 14);

    public ExpensePanel(User user, ExpenseService expenseService) {
        this.currentUser = user;
        this.expenseService = expenseService;
        
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initializeComponents();
        loadExpenses();
    }
    
    private void initializeComponents() {
        setupTable();
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
    }
    
    private void setupTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Amount", "Description"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        styleTable(expenseTable);

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = expenseTable.getSelectedRow() != -1;
            updateButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
    }

    private void styleTable(JTable table) {
        table.setBackground(SECONDARY_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setFont(GLOBAL_FONT);
        table.setRowHeight(30);
        table.setGridColor(PRIMARY_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(PRIMARY_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(ACCENT_COLOR);
        header.setFont(new Font("Roboto", Font.BOLD, 16));
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);

        JButton addButton = new JButton("Add Expense");
        styleButton(addButton);
        addButton.addActionListener(e -> showAddExpenseDialog());
        controlPanel.add(addButton);

        updateButton = new JButton("Update Expense");
        styleButton(updateButton);
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> showUpdateExpenseDialog());
        controlPanel.add(updateButton);

        deleteButton = new JButton("Delete Expense");
        styleButton(deleteButton);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteExpense());
        controlPanel.add(deleteButton);

        return controlPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private void loadExpenses() {
        SwingWorker<List<Expense>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Expense> doInBackground() throws Exception {
                return expenseService.getExpensesForUser(currentUser);
            }

            @Override
            protected void done() {
                try {
                    List<Expense> expenses = get();
                    updateTableModel(expenses);
                } catch (Exception e) {
                    ExceptionHandler.handleDatabaseException(e, "Failed to load expenses");
                }
            }
        };
        worker.execute();
    }

    private void updateTableModel(List<Expense> expenses) {
        tableModel.setRowCount(0);
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getExpenseDate(),
                expense.getCategoryName(),
                expense.getAmount(),
                expense.getDescription()
            });
        }
    }

    public void showAddExpenseDialog() {
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        JComboBox<ExpenseCategory> categoryBox = new JComboBox<>();
        
        try {
            List<ExpenseCategory> categories = expenseService.getAllCategories();
            categoryBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new ExpenseCategory[0])));
        } catch (DatabaseException e) {
            ExceptionHandler.handleDatabaseException(e, "Failed to load categories");
            return; 
        }

        final JComponent[] inputs = new JComponent[] {
            new JLabel("Description"),
            descriptionField,
            new JLabel("Amount"),
            amountField,
            new JLabel("Category"),
            categoryBox
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Add New Expense", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                ExpenseCategory selectedCategory = (ExpenseCategory) categoryBox.getSelectedItem();
                
                if (selectedCategory != null) {
                    expenseService.createExpense(currentUser.getId(), selectedCategory.getId(), amount, description, LocalDate.now());
                    loadExpenses(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to create expense");
            }
        }
    }

    private void showUpdateExpenseDialog() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);

        
        JTextField descriptionField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
        JTextField amountField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JComboBox<ExpenseCategory> categoryBox = new JComboBox<>();
        
        try {
            List<ExpenseCategory> categories = expenseService.getAllCategories();
            categoryBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new ExpenseCategory[0])));
            String currentCategoryName = (String) tableModel.getValueAt(selectedRow, 2);
            for (ExpenseCategory category : categories) {
                if (category.getName().equals(currentCategoryName)) {
                    categoryBox.setSelectedItem(category);
                    break;
                }
            }
        } catch (DatabaseException e) {
            ExceptionHandler.handleDatabaseException(e, "Failed to load categories");
            return; 
        }

        final JComponent[] inputs = new JComponent[] {
            new JLabel("Description"),
            descriptionField,
            new JLabel("Amount"),
            amountField,
            new JLabel("Category"),
            categoryBox
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Update Expense", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                ExpenseCategory selectedCategory = (ExpenseCategory) categoryBox.getSelectedItem();
                
                if (selectedCategory != null) {
                    Expense updatedExpense = new Expense(currentUser.getId(), selectedCategory.getId(), amount, description, LocalDate.now());
                    updatedExpense.setId(expenseId);
                    expenseService.updateExpense(updatedExpense);
                    loadExpenses(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to update expense");
            }
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                expenseService.deleteExpense(expenseId);
                loadExpenses();
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to delete expense");
            }
        }
    }
    
}
