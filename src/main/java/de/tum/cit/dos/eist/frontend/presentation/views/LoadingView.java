package de.tum.cit.dos.eist.frontend.presentation.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tum.cit.dos.eist.frontend.presentation.components.Theme;

public class LoadingView {
    public static JPanel getBody() {
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Theme.backgroundColor);

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setForeground(Color.WHITE);

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(loadingLabel);
        box.add(Box.createHorizontalGlue());

        loadingPanel.add(box, BorderLayout.PAGE_START);
        return loadingPanel;
    }
}
