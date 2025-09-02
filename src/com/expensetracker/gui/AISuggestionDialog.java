package com.expensetracker.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.expensetracker.model.Expense;
import com.expensetracker.service.OllamaClient;


public class AISuggestionDialog extends JDialog {
    

    private static final Color PRIMARY_COLOR = new Color(25, 25, 112);
    private static final Color SECONDARY_COLOR = new Color(135, 206, 250);
    private static final Color ACCENT_COLOR = new Color(255, 255, 255);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    
    private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Roboto", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 12);
    
    private JTextArea insightArea;
    private JButton closeButton;
    private JButton refreshButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private List<Expense> expenses; 
    
    public AISuggestionDialog(JFrame parent, List<Expense> expenses) {
        super(parent, "AI Expense Analysis", true);
        this.expenses = expenses; 
        initializeDialog();
        loadAIInsight(expenses);
    }
    

    private void initializeDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getOwner());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            "Close",
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        JPanel mainPanel = new GradientPanel(new Color[]{BACKGROUND_COLOR, new Color(240, 242, 245)}, 0);
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
   
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        // Title label
        JLabel titleLabel = new JLabel("🤖 AI Expense Analysis");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Intelligent insights for your business expenses");
        subtitleLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
   
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        statusLabel = new JLabel("Analyzing your expenses...");
        statusLabel.setFont(BODY_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setVisible(true);
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);
        
     
        insightArea = new JTextArea();
        insightArea.setEditable(false);
        insightArea.setLineWrap(true);
        insightArea.setWrapStyleWord(true);
        insightArea.setFont(BODY_FONT);
        insightArea.setForeground(TEXT_COLOR);
        insightArea.setBackground(ACCENT_COLOR);
        insightArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        
        JScrollPane scrollPane = new JScrollPane(insightArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
       
        insightArea.setText("Loading AI analysis...\n\nPlease wait while our AI analyzes your expense patterns and provides actionable insights.");
        
        contentPanel.add(statusPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }
    

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Refresh button
        refreshButton = new JButton("🔄 Refresh Analysis");
        styleButton(refreshButton, SECONDARY_COLOR);
        refreshButton.setEnabled(false);
        
        // Close button
        closeButton = new JButton("✕ Close");
        styleButton(closeButton, PRIMARY_COLOR);
        
        // Add action listeners
        closeButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> {
            // Refresh the AI analysis
            refreshAnalysis();
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
    

    private void loadAIInsight(List<Expense> expenses) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
              
                Thread.sleep(1000);
                
                OllamaClient ollamaClient = new OllamaClient();
                return ollamaClient.getOverallInsight(expenses);
            }
            
            @Override
            protected void done() {
                try {
                    String insight = get();
                    
                    if (insight.startsWith("AI service unavailable:")) {
                        showError("AI Service Error", insight + "\n\nPlease make sure the ollama_server.py is running.");
                        return;
                    }
                    
                    
                    String formattedInsight = formatInsight(insight);
                    insightArea.setText(formattedInsight);
                    
                    // Update status
                    statusLabel.setText("Analysis complete");
                    progressBar.setVisible(false);
                    refreshButton.setEnabled(true);
                    
                } catch (Exception ex) {
                    showError("Error", "Failed to get AI suggestion: " + ex.getMessage());
                }
            }
        };
        
        worker.execute();
    }

    private void refreshAnalysis() {
       
        refreshButton.setEnabled(false);
        statusLabel.setText("Refreshing analysis...");
        progressBar.setVisible(true);
        
     
        insightArea.setText("Refreshing AI analysis...\n\nPlease wait while our AI re-analyzes your expense patterns and provides updated insights.");
        
      
        loadAIInsight(expenses);
    }
    
    
    private String formatInsight(String insight) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("📊 EXPENSE ANALYSIS REPORT\n");
        formatted.append("=".repeat(50)).append("\n\n");
        
      
        String[] lines = insight.split("\n");
        boolean inSection = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            
            if (line.startsWith("**") || line.matches("^\\d+\\..*")) {
                if (inSection) formatted.append("\n");
                formatted.append("🔹 ").append(line.replace("**", "")).append("\n");
                formatted.append("-".repeat(30)).append("\n");
                inSection = true;
            } else if (line.startsWith("*") || line.startsWith("-")) {
                formatted.append("  • ").append(line.replace("*", "").replace("-", "").trim()).append("\n");
            } else {
                formatted.append(line).append("\n");
            }
        }
        
        formatted.append("\n").append("=".repeat(50)).append("\n");
        formatted.append("💡 Generated by Anup's Agent\n");
        
        return formatted.toString();
    }
    
    private void showError(String title, String message) {
        statusLabel.setText("Error occurred");
        progressBar.setVisible(false);
        
        JTextArea errorArea = new JTextArea(message);
        errorArea.setEditable(false);
        errorArea.setLineWrap(true);
        errorArea.setWrapStyleWord(true);
        errorArea.setFont(BODY_FONT);
        errorArea.setBackground(new Color(248, 215, 218));
        errorArea.setForeground(new Color(114, 28, 36));
        errorArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(errorArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.ERROR_MESSAGE);
    }
}
