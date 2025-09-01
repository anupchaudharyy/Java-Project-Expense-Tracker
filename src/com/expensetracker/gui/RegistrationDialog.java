package com.expensetracker.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import com.expensetracker.utils.ExceptionHandler;

public class RegistrationDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private UserDAO userDAO;

    // New aesthetic color scheme
    private final Color PRIMARY_COLOR = new Color(25, 25, 112); // Midnight Blue
    private final Color SECONDARY_COLOR = new Color(135, 206, 250); // Light Sky Blue
    private final Color TEXT_COLOR = new Color(240, 248, 255); // Alice Blue
    private final Color ACCENT_COLOR = new Color(255, 165, 0); // Orange
    private final Color TEXT_FIELD_BG = new Color(255, 255, 255, 50); // Semi-transparent white

    public RegistrationDialog(JFrame parent) {
        super(parent, "Create Account", true);
        this.userDAO = new UserDAO();
        
        initializeGUI();
        setupComponents();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setSize(400, 600);
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

        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        usernameField = new JTextField(20);
        styleTextField(usernameField, "Username");
        add(usernameField, gbc);
        
        passwordField = new JPasswordField(20);
        styleTextField(passwordField, "Password");
        add(passwordField, gbc);

        confirmPasswordField = new JPasswordField(20);
        styleTextField(confirmPasswordField, "Confirm Password");
        add(confirmPasswordField, gbc);
        
        registerButton = new JButton("REGISTER");
        styleButton(registerButton);
        add(registerButton, gbc);
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
        registerButton.addActionListener(e -> performRegistration());
        confirmPasswordField.addActionListener(e -> performRegistration());
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String failMessage = "";
            @Override
            protected Boolean doInBackground() throws Exception {
                if (userDAO.getUserByUsername(username) != null) {
                    failMessage = "Username is already taken.";
                    return false;
                }
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password); // Remember to hash passwords in a real application
                newUser.setRole(User.Role.STAFF);
                userDAO.createUser(newUser);
                return true;
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RegistrationDialog.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegistrationDialog.this, failMessage, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    ExceptionHandler.handleDatabaseException(e, "Registration");
                }
            }
        };
        worker.execute();
    }
}