package com.expensetracker.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import com.expensetracker.utils.ExceptionHandler;

public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    private User authenticatedUser;
    private UserDAO userDAO;

   
    private final Color PRIMARY_COLOR = new Color(25, 25, 112); 
    private final Color SECONDARY_COLOR = new Color(135, 206, 250); 
    private final Color TEXT_COLOR = new Color(240, 248, 255); 
    private final Color ACCENT_COLOR = new Color(255, 165, 0); 
    private final Color TEXT_FIELD_BG = new Color(255, 255, 255, 50); 

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        this.userDAO = new UserDAO();
        
        initializeGUI();
        setupComponents();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setSize(800, 800);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        GradientPanel contentPane = new GradientPanel(new Color[]{PRIMARY_COLOR, SECONDARY_COLOR}, 135);
        setContentPane(contentPane);
    }
    
    private void setupComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 25, 15, 25);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Smart Expense Tracker");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);


        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 25, 0, 25);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(TEXT_COLOR);
        add(usernameLabel, gbc);

        // Username Field
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 25, 15, 25);
        usernameField = new JTextField(20);
        styleTextField(usernameField, "Username");
        add(usernameField, gbc);
        
        // Password Label
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 25, 0, 25);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(TEXT_COLOR);
        add(passwordLabel, gbc);

        // Password Field
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 25, 15, 25);
        passwordField = new JPasswordField(20);
        styleTextField(passwordField, "Password");
        add(passwordField, gbc);

        gbc.insets = new Insets(15, 25, 15, 25);
        
        // Login Button
        loginButton = new JButton("LOGIN");
        styleButton(loginButton);
        add(loginButton, gbc);

        // Registration 
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setForeground(TEXT_COLOR);
        noAccountLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
        
        registerLabel = new JLabel("Register");
        registerLabel.setForeground(ACCENT_COLOR);
        registerLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLabel);
        add(registerPanel, gbc);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Roboto", Font.PLAIN, 14));
        field.setBackground(TEXT_FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
     
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());

        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegistrationDialog registrationDialog = new RegistrationDialog((JFrame) getParent());
                registrationDialog.setVisible(true);
            }
        });
        
        passwordField.addActionListener(e -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        authenticatedUser = user;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    ExceptionHandler.handleDatabaseException(e, "Login");
                }
            }
        };
        worker.execute();
    }
    
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}