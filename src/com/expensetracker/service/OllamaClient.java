package com.expensetracker.service;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.expensetracker.model.Expense;


public class OllamaClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final int TIMEOUT = 30000; 
    
  
    public String getOverallInsight(List<Expense> expenses) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
            // Create socket connection
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(TIMEOUT);
            
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            
            JSONObject request = new JSONObject();
            request.put("description", buildPrompt(expenses));
            
           
            out.println(request.toJSONString());
            
        
            String response = in.readLine();
            
            if (response == null || response.trim().isEmpty()) {
                return "AI service returned empty response. Please check if the Python server is running properly.";
            }
            
          
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            
            String prediction = (String) jsonResponse.get("prediction");
            if (prediction == null) {
                return "AI service returned invalid response format.";
            }
            
            return prediction;
            
        } catch (java.net.ConnectException e) {
            return "AI service unavailable: Cannot connect to Python server on " + HOST + ":" + PORT + 
                   ". Please make sure ollama_server.py is running.";
        } catch (java.net.SocketTimeoutException e) {
            return "AI service timeout: Request took too long. Please try again.";
        } catch (Exception e) {
            return "AI service error: " + e.getMessage() + ". Please check the Python server logs.";
        } finally {
            
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket resources: " + e.getMessage());
            }
        }
    }

    public String getExpenseInsight(String description) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
            
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(TIMEOUT);
            
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            
            JSONObject request = new JSONObject();
            request.put("description", description);
            
          
            out.println(request.toJSONString());
            
          
            String response = in.readLine();
            
            if (response == null || response.trim().isEmpty()) {
                return "AI service returned empty response. Please check if the Python server is running properly.";
            }
            
            
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            
            String prediction = (String) jsonResponse.get("prediction");
            if (prediction == null) {
                return "AI service returned invalid response format.";
            }
            
            return prediction;
            
        } catch (java.net.ConnectException e) {
            return "AI service unavailable: Cannot connect to Python server on " + HOST + ":" + PORT + 
                   ". Please make sure ollama_server.py is running.";
        } catch (java.net.SocketTimeoutException e) {
            return "AI service timeout: Request took too long. Please try again.";
        } catch (Exception e) {
            return "AI service error: " + e.getMessage() + ". Please check the Python server logs.";
        } finally {
            
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket resources: " + e.getMessage());
            }
        }
    }

    private String buildPrompt(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return "There are no expenses to analyze.";
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a business expense analyst. Here are the expenses to analyze:\n\n");

        // Group expenses by category for better analysis
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategoryName();
            categoryTotals.merge(category, expense.getAmount(), BigDecimal::add);
        }

        // Add expense details
        for (Expense expense : expenses) {
            prompt.append(String.format("- %s: $%.2f (%s)\n",
                expense.getDescription(), expense.getAmount(), expense.getCategoryName()));
        }

        prompt.append("\nAnalyze these expenses and provide:\n");
        prompt.append("1. A brief summary of spending patterns\n");
        prompt.append("2. The highest spending category\n");
        prompt.append("3. 2-3 specific cost optimization suggestions\n");
        prompt.append("\nFormat your response clearly and be concise.");

        return prompt.toString();
    }
}
