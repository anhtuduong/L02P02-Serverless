package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserDetails extends JPanel {
    public UserDetails(String name, String location) {
        JPanel userDetails = this;
        userDetails.setLayout(new BoxLayout(userDetails, BoxLayout.X_AXIS));
        userDetails.setAlignmentX(Component.RIGHT_ALIGNMENT);
        userDetails.setBackground(Theme.backgroundColor);

        JPanel textDetails = new JPanel();
        textDetails.setLayout(new BoxLayout(textDetails, BoxLayout.Y_AXIS));
        textDetails.setBackground(Theme.backgroundColor);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font(Theme.fontName, Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        JLabel locationLabel = new JLabel(location);
        locationLabel.setFont(new Font(Theme.fontName, Font.PLAIN, 14));
        locationLabel.setForeground(Color.LIGHT_GRAY);

        // Add padding to the bottom
        userDetails.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        textDetails.add(nameLabel);
        textDetails.add(locationLabel);

        userDetails.add(textDetails);
    }
}
