package com.expensetracker.gui;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ChartUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class MyChartPanel extends JPanel {

    private JFreeChart chart; 
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public MyChartPanel() {
        initUI();
    }

    private void initUI() {
        chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Income vs Expense",  // chart title
                "Category",
                "Amount",
                dataset,             // data
                PlotOrientation.VERTICAL,
                true,                // include legend
                true,
                false);

        chart.setBackgroundPaint(new Color(0,0,0,0)); // Transparent background
        chart.getTitle().setPaint(Color.WHITE);
        chart.getLegend().setBackgroundPaint(new Color(0,0,0,0));
        chart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.setOutlineVisible(false);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.setNoDataMessage("No data available for the selected period.");
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 255, 0, 180)); // Income
        renderer.setSeriesPaint(1, new Color(255, 0, 0, 180)); // Expense

        return chart;
    }

    public void updateChartData(Map<String, BigDecimal> incomeData, Map<String, BigDecimal> expenseData) {
        dataset.clear();
        
        Set<String> categories = new HashSet<>();
        if (incomeData != null) {
            categories.addAll(incomeData.keySet());
        }
        if (expenseData != null) {
            categories.addAll(expenseData.keySet());
        }

        for (String category : categories) {
            BigDecimal income = incomeData != null ? incomeData.getOrDefault(category, BigDecimal.ZERO) : BigDecimal.ZERO;
            BigDecimal expense = expenseData != null ? expenseData.getOrDefault(category, BigDecimal.ZERO) : BigDecimal.ZERO;
            dataset.addValue(income, "Income", category);
            dataset.addValue(expense, "Expense", category);
        }
    }

    public void saveChartAsPNG(File file) throws IOException {
        ChartUtils.saveChartAsPNG(file, chart, 800, 600);
    }
}
