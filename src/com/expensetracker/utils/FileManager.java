package com.expensetracker.utils;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import com.expensetracker.model.Expense;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class FileManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
 
    public static boolean exportToCSV(List<Expense> expenses, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            
            writer.println("ID,Date,Category,Description,Amount");
            
           
            for (Expense expense : expenses) {
                writer.printf("%d,%s,%s,\"%s\",%.2f%n",
                    expense.getId(),
                    expense.getExpenseDate().format(DATE_FORMATTER),
                    expense.getCategoryName(),
                    expense.getDescription().replace("\"", "\"\""), // Escape quotes
                    expense.getAmount()
                );
            }
            
            System.out.println("Exported " + expenses.size() + " expenses to " + filename);
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to export CSV: " + e.getMessage());
            return false;
        }
    }
    
   
    public static List<Expense> importFromCSV(String filename) throws IOException {
        List<Expense> expenses = new java.util.ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(filename))) {
            // Skip header line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = parseCSVLine(line);
                
                if (parts.length >= 5) {
                    try {
                        Expense expense = new Expense();
                        expense.setId(Integer.parseInt(parts[0]));
                        expense.setExpenseDate(LocalDate.parse(parts[1], DATE_FORMATTER));
                        expense.setCategoryName(parts[2]);
                        expense.setDescription(parts[3]);
                        expense.setAmount(new BigDecimal(parts[4]));
                        
                        expenses.add(expense);
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        
        System.out.println("Imported " + expenses.size() + " expenses from " + filename);
        return expenses;
    }
    
  
    private static String[] parseCSVLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
    

    @SuppressWarnings("unchecked")
    public static boolean exportToJSON(List<Expense> expenses, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            JSONArray jsonArray = new JSONArray();
            
            for (Expense expense : expenses) {
                JSONObject jsonExpense = new JSONObject();
                jsonExpense.put("id", expense.getId());
                jsonExpense.put("date", expense.getExpenseDate().toString());
                jsonExpense.put("category", expense.getCategoryName());
                jsonExpense.put("description", expense.getDescription());
                jsonExpense.put("amount", expense.getAmount().toString());
                
                jsonArray.add(jsonExpense);
            }
            
            writer.write(jsonArray.toJSONString());
            System.out.println("Exported " + expenses.size() + " expenses to JSON: " + filename);
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to export JSON: " + e.getMessage());
            return false;
        }
    }
    
    public static List<Expense> importFromJSON(String filename) throws IOException {
        List<Expense> expenses = new java.util.ArrayList<>();
        
        try (FileReader reader = new FileReader(filename)) {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            
            for (Object obj : jsonArray) {
                JSONObject jsonExpense = (JSONObject) obj;
                
                Expense expense = new Expense();
                expense.setId(((Long) jsonExpense.get("id")).intValue());
                expense.setExpenseDate(LocalDate.parse((String) jsonExpense.get("date")));
                expense.setCategoryName((String) jsonExpense.get("category"));
                expense.setDescription((String) jsonExpense.get("description"));
                expense.setAmount(new BigDecimal((String) jsonExpense.get("amount")));
                
                expenses.add(expense);
            }
            
        } catch (Exception e) {
            throw new IOException("Failed to parse JSON file", e);
        }
        
        System.out.println("Imported " + expenses.size() + " expenses from JSON: " + filename);
        return expenses;
    }
}