package com.expensetracker.main;

import javax.swing.*;
import java.awt.EventQueue;
import com.expensetracker.gui.LoginDialog;
import com.expensetracker.gui.MainFrame;
import com.expensetracker.model.User;
import com.expensetracker.utils.ExceptionHandler;
import com.formdev.flatlaf.FlatLightLaf;


public class ExpenseTrackerApp {
    
  
    public static void main(String[] args) {
       
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
          
            System.err.println("Could not set FlatLaf look and feel: " + e.getMessage());
        }
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                startApplication();
            }
        });
    }
    

    private static void startApplication() {
        try {
            
            showSplashScreen();
            
            
            JFrame dummyFrame = new JFrame();
            dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            LoginDialog loginDialog = new LoginDialog(dummyFrame);
            loginDialog.setVisible(true);

            User user = loginDialog.getAuthenticatedUser();
            
            if (user != null) {
             
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
             
                dummyFrame.dispose();
                
            } else {
             
                System.exit(0);
            }
            
        } catch (Exception e) {
            ExceptionHandler.handleGeneralException(e, "Application startup");
            System.exit(1);
        }
    }
    

    public static void restartApplication(JFrame currentFrame) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        startApplication();
    }

    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        
      
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createRaisedBevelBorder());
        content.setBackground(java.awt.Color.WHITE);
   
        JLabel titleLabel = new JLabel("Smart Expense Tracker");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
     
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
    
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
     
        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createRigidArea(new java.awt.Dimension(0, 10)));
        content.add(versionLabel);
        content.add(Box.createRigidArea(new java.awt.Dimension(0, 20)));
        content.add(loadingLabel);
        content.add(Box.createRigidArea(new java.awt.Dimension(0, 10)));
        content.add(progressBar);
        content.add(Box.createVerticalGlue());
        
        splash.setContentPane(content);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
      
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        splash.dispose();
    }
}