package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;

public class PostedImage extends JLabel {
    public PostedImage(String imageUrl) {
        // Image
        try {
            ImageIcon imageIcon = null;
            if(imageUrl.startsWith("/images")) {
                imageIcon = new ImageIcon(BeUnrealApp.class.getResource(imageUrl));
            } else {
                imageIcon = new ImageIcon(new URL(imageUrl));
            }

            Image image = imageIcon.getImage();
            Image newimg = image.getScaledInstance(428, 428, Image.SCALE_DEFAULT);
            ImageIcon scaledIcon = new ImageIcon(newimg);
            setIcon(scaledIcon);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
