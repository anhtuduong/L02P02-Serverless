package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import de.tum.cit.dos.eist.frontend.models.Post;

public class PostPanel extends JPanel {
    public PostPanel(Post post, boolean hasUserAlreadyPosted) {
        JPanel postPanel = this;

        // Use GridBagLayout to centralize components
        GridBagLayout layout = new GridBagLayout();
        postPanel.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH; // ensure components stretch to fill available space
        constraints.anchor = GridBagConstraints.CENTER; // centralize components

        postPanel.setBackground(Theme.backgroundColor);
        JPanel userDetails = new UserDetails(post.user().name(), post.location());
        postPanel.add(userDetails, constraints);

        // Increase y-index for the next component
        constraints.gridy++;

        JLayeredPane imageLayeredPane = new JLayeredPane();
        imageLayeredPane.setPreferredSize(new Dimension(428, 428));
        postPanel.add(imageLayeredPane, constraints);

        PostedImage postedImage = new PostedImage(post.presignedImageUrl());
        postedImage.setBounds(0, 0, 428, 428);
        imageLayeredPane.add(postedImage, JLayeredPane.DEFAULT_LAYER);

        if (!hasUserAlreadyPosted) {
            NotPostedOverlay overlay = new NotPostedOverlay();
            overlay.setBounds(0, 0, 428, 428);
            imageLayeredPane.add(overlay, JLayeredPane.PALETTE_LAYER);
        }

        postPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 48, 0));
    }
}
