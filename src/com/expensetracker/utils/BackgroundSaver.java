package com.expensetracker.utils;

import com.expensetracker.service.ExpenseService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundSaver {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ExpenseService expenseService;
    private volatile boolean isRunning = false;
    
    public BackgroundSaver(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    

    public void startAutoSave() {
        if (isRunning) return;
        
        isRunning = true;
        
        scheduler.scheduleWithFixedDelay(new AutoSaveTask(), 0, 5, TimeUnit.MINUTES);
        
        scheduler.scheduleWithFixedDelay(new ReportGenerationTask(), 0, 1, TimeUnit.HOURS);
        
        System.out.println("Background services started");
    }
    
    
    public void stopAutoSave() {
        isRunning = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Background services stopped");
    }
    
 
    private class AutoSaveTask implements Runnable {
        @Override
        public void run() {
            try {
              
                System.out.println("Auto-saving data at: " + new java.util.Date());
                
              
                
            } catch (Exception e) {
                System.err.println("Auto-save failed: " + e.getMessage());
            }
        }
    }
    

    private class ReportGenerationTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Generating reports at: " + new java.util.Date());
                
                // This prevents UI blocking during heavy computations
                
            } catch (Exception e) {
                System.err.println("Report generation failed: " + e.getMessage());
            }
        }
    }
}