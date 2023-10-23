package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class Logo extends JLabel {
    public Logo() {
        JLabel logoLabel = this;
        logoLabel.setText(Theme.appName);
        logoLabel.setFont(new Font(Theme.fontName, Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    }
}
