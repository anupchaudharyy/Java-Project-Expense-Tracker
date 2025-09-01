package com.expensetracker.gui;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.OllamaClient;
import com.expensetracker.utils.BackgroundSaver;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private User currentUser;
    private ExpenseService expenseService;
    private BackgroundSaver backgroundSaver;
    
    private JTabbedPane tabbedPane;
    private ExpensePanel expensePanel;
    private ReportsPanel reportsPanel;
    private JLabel statusLabel;

    // UI Colors and Fonts
    private final Color PRIMARY_COLOR = new Color(25, 25, 112); 
    private final Color SECONDARY_COLOR = new Color(135, 206, 250);
    private final Font GLOBAL_FONT = new Font("Roboto", Font.PLAIN, 14);
    private final Color TEXT_COLOR = Color.WHITE;

    public MainFrame(User user) {
        this.currentUser = user;
        this.expenseService = new ExpenseService();
        this.backgroundSaver = new BackgroundSaver(expenseService);
        
        initializeGUI();
        setupMenuBar();
        setupComponents();
        setupEventHandlers();
        
        backgroundSaver.startAutoSave();
    }
    
    private void initializeGUI() {
        setTitle("Smart Expense Tracker - Welcome, " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null);
        
        GradientPanel mainPanel = new GradientPanel(new Color[]{PRIMARY_COLOR, SECONDARY_COLOR}, 45);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(GLOBAL_FONT);
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.setFont(GLOBAL_FONT);
        fileMenu.setForeground(TEXT_COLOR);
        
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> reportsPanel.saveReport());
        fileMenu.add(saveItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                com.expensetracker.main.ExpenseTrackerApp.restartApplication(this);
            }
        });
        fileMenu.add(logoutItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);

        JMenu expensesMenu = new JMenu("Expenses");
        expensesMenu.setMnemonic('E');
        expensesMenu.setFont(GLOBAL_FONT);
        expensesMenu.setForeground(TEXT_COLOR);
        
        JMenuItem addExpenseItem = new JMenuItem("Add Expense");
        addExpenseItem.addActionListener(e -> expensePanel.showAddExpenseDialog());
        expensesMenu.add(addExpenseItem);

        menuBar.add(expensesMenu);

        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('A');
        aboutMenu.setFont(GLOBAL_FONT);
        aboutMenu.setForeground(TEXT_COLOR);

        JMenuItem aboutItem = new JMenuItem("About System");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Smart Expense Tracker is a Java Swing desktop app that lets users record and analyze expenses with interactive charts. It also integrates AI-powered tips to suggest smarter savings based on spending habits.", "About", JOptionPane.INFORMATION_MESSAGE));
        aboutMenu.add(aboutItem);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }
    
    private void setupComponents() {
        // AI Suggestion Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton aiSuggestionButton = new JButton("🤖 Get AI Suggestion");
        
        // Style the AI suggestion button to make it more prominent
        aiSuggestionButton.setFont(new Font("Roboto", Font.BOLD, 14));
        aiSuggestionButton.setBackground(new Color(25, 25, 112));
        aiSuggestionButton.setForeground(Color.WHITE);
        aiSuggestionButton.setFocusPainted(false);
        aiSuggestionButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 25, 112).darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Add hover effect
        aiSuggestionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aiSuggestionButton.setBackground(new Color(25, 25, 112).brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aiSuggestionButton.setBackground(new Color(25, 25, 112));
            }
        });
        
        aiSuggestionButton.addActionListener(e -> {
            try {
                List<Expense> expenses = expenseService.getExpensesForUser(currentUser);
                if (expenses.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No expenses to analyze.", "AI Suggestion", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Use the new custom AI suggestion dialog
                AISuggestionDialog aiDialog = new AISuggestionDialog(this, expenses);
                aiDialog.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to get AI suggestion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        topPanel.add(aiSuggestionButton);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Roboto", Font.BOLD, 14));
        tabbedPane.setOpaque(false);

        // Panels
        expensePanel = new ExpensePanel(currentUser, expenseService);
        reportsPanel = new ReportsPanel(currentUser, expenseService);

        tabbedPane.addTab("Dashboard", new ImageIcon("path/to/dashboard_icon.png"), expensePanel, "View and manage expenses");
        tabbedPane.addTab("Financial Reports", new ImageIcon("path/to/reports_icon.png"), reportsPanel, "Analyze your spending");

        // Status Bar
        statusLabel = new JLabel("Welcome, " + currentUser.getUsername());
        statusLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            backgroundSaver.stopAutoSave();
            System.exit(0);
        }
    }
    
}
