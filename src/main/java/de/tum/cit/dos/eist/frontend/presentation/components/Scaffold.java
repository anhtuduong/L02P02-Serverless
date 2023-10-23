package de.tum.cit.dos.eist.frontend.presentation.components;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Scaffold extends JScrollPane {
    private static Scaffold instance;
    JPanel panel;

    private Scaffold(JComponent... components) {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.backgroundColor);

        panel.add(new Logo());
        panel.add(new ActionRow());

        for (JComponent component : components) {
            panel.add(component);
        }

        setViewportView(panel);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public static Scaffold getInstance() {
        if (instance == null) {
            instance = new Scaffold();
        }
        return instance;
    }

    public void requestScrollFocus() {
        System.out.println("Request focus");
        requestFocusInWindow();
        setWheelScrollingEnabled(true);
    }

    // Rebuilds the UI with the components.
    public void rebuild(JComponent... components) {
        panel.removeAll();

        panel.add(new Logo());
        panel.add(new ActionRow());

        for (JComponent component : components) {
            panel.add(component);
        }

        panel.revalidate();
        panel.repaint();
    }
}
