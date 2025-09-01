package com.expensetracker.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;


public class ExceptionHandler {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
   
    public static void handleDatabaseException(Exception e, String operation) {
        String userMessage = "Database operation failed: " + operation;
        String logMessage = String.format("[%s] DATABASE ERROR in %s: %s", 
            LocalDateTime.now().format(TIMESTAMP_FORMAT), operation, e.getMessage());
        
       
        System.err.println(logMessage);
        e.printStackTrace();
        
     
        JOptionPane.showMessageDialog(null, 
            userMessage + "\n\nPlease check your database connection and try again.", 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    
    public static void handleValidationException(Exception e) {
        String message = e.getMessage();
        System.err.println("Validation Error: " + message);
        
        JOptionPane.showMessageDialog(null, 
            "Input Error: " + message, 
            "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
    }
    
 
    public static void handleGeneralException(Exception e, String context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logMessage = String.format("[%s] ERROR in %s: %s", timestamp, context, e.getMessage());
        
        
        System.err.println(logMessage);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        System.err.println(sw.toString());
        
        
        JOptionPane.showMessageDialog(null, 
            "An unexpected error occurred: " + e.getMessage() + 
            "\n\nPlease check the console for details.", 
            "Application Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    

    public static void handleNetworkException(Exception e, String operation) {
        String userMessage = "Network operation failed: " + operation;
        String logMessage = String.format("[%s] NETWORK ERROR in %s: %s", 
            LocalDateTime.now().format(TIMESTAMP_FORMAT), operation, e.getMessage());
        
        System.err.println(logMessage);
        
        JOptionPane.showMessageDialog(null, 
            userMessage + "\n\nPlease check your network connection and try again.", 
            "Network Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}