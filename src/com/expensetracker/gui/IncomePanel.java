package com.expensetracker.gui;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.model.Income;
import com.expensetracker.model.IncomeCategory;
import com.expensetracker.model.User;
import com.expensetracker.service.IncomeService;
import com.expensetracker.utils.ExceptionHandler;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class IncomePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private User currentUser;
    private IncomeService incomeService;
    
    private JTable incomeTable;
    private DefaultTableModel tableModel;
    private JButton updateButton;
    private JButton deleteButton;
    
    private final Color PRIMARY_COLOR = new Color(40, 44, 52);
    private final Color SECONDARY_COLOR = new Color(33, 37, 43);
    private final Color ACCENT_COLOR = new Color(97, 218, 251);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font GLOBAL_FONT = new Font("Roboto", Font.PLAIN, 14);

    public IncomePanel(User user, IncomeService incomeService) {
        this.currentUser = user;
        this.incomeService = incomeService;
        
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initializeComponents();
        loadIncomes();
    }
    
    private void initializeComponents() {
        setupTable();
        add(new JScrollPane(incomeTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
    }
    
    private void setupTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Amount", "Description"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incomeTable = new JTable(tableModel);
        styleTable(incomeTable);

        incomeTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = incomeTable.getSelectedRow() != -1;
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

        JButton addButton = new JButton("Add Income");
        styleButton(addButton);
        addButton.addActionListener(e -> showAddIncomeDialog());
        controlPanel.add(addButton);

        updateButton = new JButton("Update Income");
        styleButton(updateButton);
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> showUpdateIncomeDialog());
        controlPanel.add(updateButton);

        deleteButton = new JButton("Delete Income");
        styleButton(deleteButton);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteIncome());
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

    private void loadIncomes() {
        SwingWorker<List<Income>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Income> doInBackground() throws Exception {
                return incomeService.getIncomesForUser(currentUser);
            }

            @Override
            protected void done() {
                try {
                    List<Income> incomes = get();
                    updateTableModel(incomes);
                } catch (Exception e) {
                    ExceptionHandler.handleDatabaseException(e, "Failed to load incomes");
                }
            }
        };
        worker.execute();
    }

    private void updateTableModel(List<Income> incomes) {
        tableModel.setRowCount(0);
        for (Income income : incomes) {
            tableModel.addRow(new Object[]{
                income.getId(),
                income.getIncomeDate(),
                income.getCategoryName(),
                income.getAmount(),
                income.getDescription()
            });
        }
    }

    public void showAddIncomeDialog() {
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        JComboBox<IncomeCategory> categoryBox = new JComboBox<>();
        
        try {
            List<IncomeCategory> categories = incomeService.getAllCategories();
            categoryBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new IncomeCategory[0])));
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
        int result = JOptionPane.showConfirmDialog(this, inputs, "Add New Income", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                IncomeCategory selectedCategory = (IncomeCategory) categoryBox.getSelectedItem();
                
                if (selectedCategory != null) {
                    incomeService.createIncome(currentUser.getId(), selectedCategory.getId(), amount, description, LocalDate.now());
                    loadIncomes(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to create income");
            }
        }
    }

    private void showUpdateIncomeDialog() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int incomeId = (int) tableModel.getValueAt(selectedRow, 0);

        
        JTextField descriptionField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
        JTextField amountField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JComboBox<IncomeCategory> categoryBox = new JComboBox<>();
        
        try {
            List<IncomeCategory> categories = incomeService.getAllCategories();
            categoryBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new IncomeCategory[0])));
            String currentCategoryName = (String) tableModel.getValueAt(selectedRow, 2);
            for (IncomeCategory category : categories) {
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
        int result = JOptionPane.showConfirmDialog(this, inputs, "Update Income", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                IncomeCategory selectedCategory = (IncomeCategory) categoryBox.getSelectedItem();
                
                if (selectedCategory != null) {
                    Income updatedIncome = new Income(currentUser.getId(), selectedCategory.getId(), amount, description, LocalDate.now());
                    updatedIncome.setId(incomeId);
                    incomeService.updateIncome(updatedIncome);
                    loadIncomes(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to update income");
            }
        }
    }

    private void deleteIncome() {
        int selectedRow = incomeTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int incomeId = (int) tableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this income?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                incomeService.deleteIncome(incomeId);
                loadIncomes();
            } catch (DatabaseException e) {
                ExceptionHandler.handleDatabaseException(e, "Failed to delete income");
            }
        }
    }
    
}