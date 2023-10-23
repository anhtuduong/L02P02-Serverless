package de.tum.cit.dos.eist.frontend;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.plaf.FontUIResource;

import de.tum.cit.dos.eist.frontend.controllers.FetchFeedController;
import de.tum.cit.dos.eist.frontend.models.Feed;
import de.tum.cit.dos.eist.frontend.presentation.components.Scaffold;
import de.tum.cit.dos.eist.frontend.presentation.components.Theme;
import de.tum.cit.dos.eist.frontend.presentation.views.ErrorView;
import de.tum.cit.dos.eist.frontend.presentation.views.PostsView;

/**
 * Main class of the BeUnreal application.
 * 
 * Execute the main method to start the application.
 */
public class BeUnrealApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(Theme.appName);
            Theme.setUIFont(new FontUIResource(new Font(Theme.fontName, 0, 16)));

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 1000);
            frame.getContentPane().setBackground(Theme.backgroundColor);

            Theme.setAppIcon(frame);

            Scaffold scaffold = Scaffold.getInstance();
            frame.add(scaffold);

            try {
                Feed feed = FetchFeedController.getInstance().fetchPosts();
                scaffold.rebuild(PostsView.getBody(feed).toArray(new JComponent[0]));
            } catch (Exception e) {
                System.err.println(e);
                scaffold.rebuild(ErrorView.getBody(e.toString()));
            }

            frame.setVisible(true);
        });
    }
}
