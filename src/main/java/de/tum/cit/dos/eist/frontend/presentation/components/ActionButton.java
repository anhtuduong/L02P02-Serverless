package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ActionButton extends JButton {
    private static final int ROUNDED_CORNER_RADIUS = 15;

    public ActionButton(String text, ImageIcon icon) {
        super(text, icon);
        // Set size of the icon
        setIconTextGap(10);
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);

        // Set padding for the button
        setMargin(new java.awt.Insets(4, 10, 4, 10));

        // Set height of the button
        setPreferredSize(new java.awt.Dimension(150, 40));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ROUNDED_CORNER_RADIUS, ROUNDED_CORNER_RADIUS);

        super.paintComponent(g);
    }
}
