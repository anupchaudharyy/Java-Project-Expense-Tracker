package com.expensetracker.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.LinearGradientPaint;

public class GradientPanel extends JPanel {

    private Color[] colors;
    private float[] fractions;
    private float angle;

    public GradientPanel() {
        this(new Color[]{new Color(230, 230, 250), new Color(176, 196, 222)}, 90);
    }

    public GradientPanel(Color[] colors, float angle) {
        this.colors = colors;
        this.angle = angle;
        this.fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            fractions[i] = (float) i / (colors.length - 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();

        double angleRad = Math.toRadians(angle);
        double x1 = 0;
        double y1 = 0;
        double x2 = width * Math.cos(angleRad);
        double y2 = height * Math.sin(angleRad);

        Point2D start = new Point2D.Double(x1, y1);
        Point2D end = new Point2D.Double(x2, y2);

        LinearGradientPaint paint = new LinearGradientPaint(start, end, fractions, colors);
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();
    }
}
