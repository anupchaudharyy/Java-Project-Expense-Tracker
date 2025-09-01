package com.expensetracker.gui;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import org.jfree.chart.ChartUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class MyChartPanel extends JPanel {

    private JFreeChart chart; 
    private final DefaultPieDataset dataset = new DefaultPieDataset();

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

    private JFreeChart createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Distribution",  // chart title
                dataset,             // data
                true,                // include legend
                true,
                false);

        chart.setBackgroundPaint(new Color(0,0,0,0)); // Transparent background
        chart.getTitle().setPaint(Color.WHITE);
        chart.getLegend().setBackgroundPaint(new Color(0,0,0,0));
        chart.getLegend().setItemPaint(Color.WHITE);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.setLabelGenerator(null); // Hide labels on slices
        plot.setOutlineVisible(false);
        plot.setNoDataMessage("No data available for the selected period.");

        return chart;
    }

    public void updateChartData(Map<String, BigDecimal> data) {
        dataset.clear();
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        } 
    }

    public void saveChartAsPNG(File file) throws IOException {
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
    }
}
