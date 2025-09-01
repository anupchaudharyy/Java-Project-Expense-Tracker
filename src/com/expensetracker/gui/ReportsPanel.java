package com.expensetracker.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.expensetracker.model.User;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.ReportService;
import com.expensetracker.utils.ExceptionHandler;

public class ReportsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private User currentUser;
    private ExpenseService expenseService;
    private ReportService reportService;
    
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private MyChartPanel chartPanel;
    private JLabel totalLabel;

    private final Color PRIMARY_COLOR = new Color(40, 44, 52);
    private final Color SECONDARY_COLOR = new Color(33, 37, 43);
    private final Color ACCENT_COLOR = new Color(97, 218, 251);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font GLOBAL_FONT = new Font("Roboto", Font.PLAIN, 14);

    public ReportsPanel(User user, ExpenseService expenseService) {
        this.currentUser = user;
        this.expenseService = expenseService;
        this.reportService = new ReportService();
        
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initializeComponents();
        generateReport();
    }
    
    private void initializeComponents() {
        add(createControlPanel(), BorderLayout.NORTH);
        chartPanel = new MyChartPanel();
        chartPanel.setOpaque(false);
        add(chartPanel, BorderLayout.CENTER);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);

        monthCombo = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        styleComboBox(monthCombo);
        styleComboBox(yearCombo);

        JButton generateButton = new JButton("Generate");
        styleButton(generateButton);
        generateButton.addActionListener(e -> generateReport());

        JButton jsonReportButton = new JButton("Show JSON Report");
        styleButton(jsonReportButton);
        jsonReportButton.addActionListener(e -> showJsonReport());

        totalLabel = new JLabel("Total Expenses: $0.00");
        totalLabel.setForeground(TEXT_COLOR);
        totalLabel.setFont(GLOBAL_FONT);

        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthCombo);
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(generateButton);
        controlPanel.add(jsonReportButton);
        controlPanel.add(totalLabel);

        return controlPanel;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(GLOBAL_FONT);
        combo.setBackground(SECONDARY_COLOR);
        combo.setForeground(TEXT_COLOR);
    }

    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private void generateReport() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (int) yearCombo.getSelectedItem();

        SwingWorker<Map<String, BigDecimal>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws Exception {
                return expenseService.getMonthlySummary(currentUser.getId(), year, month);
            }

            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> summary = get();
                    chartPanel.updateChartData(summary);
                    BigDecimal total = summary.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalLabel.setText(String.format("Total Expenses: $%.2f", total));
                } catch (Exception e) {
                    ExceptionHandler.handleDatabaseException(e, "Failed to generate report");
                }
            }
        };
        worker.execute();
    }

    private void showJsonReport() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                List<Expense> expenses = expenseService.getExpensesForUser(currentUser);
                return reportService.generateJsonReport(expenses);
            }

            @Override
            protected void done() {
                try {
                    String jsonReport = get();
                    JTextArea textArea = new JTextArea(jsonReport);
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(600, 400));
                    JOptionPane.showMessageDialog(ReportsPanel.this, scrollPane, "JSON Report", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    ExceptionHandler.handleGeneralException(e, "Failed to generate JSON report");
                }
            }
        };
        worker.execute();
    }

    public void refreshData() {
        generateReport();
    }
    
    public void showMonthlyReport() {
        // This might be redundant if refreshData is called on tab selection
        generateReport();
    }

    public void saveReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".png");
            }
            try {
                chartPanel.saveChartAsPNG(fileToSave);
                JOptionPane.showMessageDialog(this, "Report saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                ExceptionHandler.handleGeneralException(e, "Failed to save report");
            }
        }
    }
}
