package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;
import de.tum.cit.dos.eist.frontend.controllers.FetchFeedController;
import de.tum.cit.dos.eist.frontend.controllers.RewindController;
import de.tum.cit.dos.eist.frontend.controllers.SimulateController;
import de.tum.cit.dos.eist.frontend.models.Feed;
import de.tum.cit.dos.eist.frontend.presentation.views.ErrorView;
import de.tum.cit.dos.eist.frontend.presentation.views.LoadingView;
import de.tum.cit.dos.eist.frontend.presentation.views.PostsView;

public class ActionRow extends JPanel {
    public ActionRow() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Replace "path_to_icon1.png" with your actual paths
        ActionButton refreshButton = new ActionButton("Refresh",
                new ImageIcon(BeUnrealApp.class.getResource("/images/refresh-icon.png")));
        ActionButton simulateButton = new ActionButton("Simulate",
                new ImageIcon(BeUnrealApp.class.getResource("/images/simulate-icon.png")));
        ActionButton rewindButton = new ActionButton("Rewind",
                new ImageIcon(BeUnrealApp.class.getResource("/images/rewind-icon.png")));

        add(refreshButton);
        add(simulateButton);
        add(rewindButton);

        setBackground(Theme.backgroundColor);

        // Add bottom margin
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0));

        refreshButton.addActionListener(this::refreshPosts);
        simulateButton.addActionListener(this::simulate);
        rewindButton.addActionListener(this::rewind);
    }

    private void refreshPosts(ActionEvent e) {
        Scaffold.getInstance().rebuild(LoadingView.getBody());

        SwingWorker<Feed, Void> worker = new SwingWorker<Feed, Void>() {
            @Override
            protected Feed doInBackground() throws Exception {
                System.out.println("Pressed refresh button");
                return FetchFeedController.getInstance().fetchPosts();
            }

            @Override
            protected void done() {
                try {
                    Feed feed = get();
                    if (feed == null) {
                        Scaffold.getInstance().rebuild(
                                ErrorView.getBody("Could not fetch the feed, see client logs for more information."));
                    } else {
                        Scaffold.getInstance().rebuild(PostsView.getBody(feed).toArray(new JComponent[0]));
                    }
                } catch (Exception e) {
                    Scaffold.getInstance().rebuild(ErrorView.getBody(e.toString()));
                    e.printStackTrace();
                }
            }

        };

        worker.execute();
    }

    private void simulate(ActionEvent e) {
        Scaffold.getInstance().rebuild(LoadingView.getBody());

        SwingWorker<Feed, Void> worker = new SwingWorker<Feed, Void>() {
            @Override
            protected Feed doInBackground() throws Exception {
                SimulateController simulateController = SimulateController.getInstance();
                simulateController.simulate();

                FetchFeedController controller = FetchFeedController.getInstance();
                return controller.fetchPosts();
            }

            @Override
            protected void done() {
                try {
                    Feed feed = get();
                    if (feed == null) {
                        Scaffold.getInstance().rebuild(
                                ErrorView.getBody("Could not fetch the feed, see client logs for more information."));
                    } else {
                        Scaffold.getInstance().rebuild(PostsView.getBody(feed).toArray(new JComponent[0]));
                    }
                } catch (Exception e) {
                    Scaffold.getInstance().rebuild(ErrorView.getBody(e.toString()));
                    e.printStackTrace();
                }
            }

        };

        worker.execute();
    }

    private void rewind(ActionEvent e) {
        Scaffold.getInstance().rebuild(LoadingView.getBody());

        SwingWorker<Feed, Void> worker = new SwingWorker<Feed, Void>() {
            @Override
            protected Feed doInBackground() throws Exception {
                RewindController rewindController = RewindController.getInstance();
                rewindController.rewind();

                FetchFeedController controller = FetchFeedController.getInstance();
                return controller.fetchPosts();
            }

            @Override
            protected void done() {
                try {
                    Feed feed = get();
                    if (feed == null) {
                        Scaffold.getInstance().rebuild(
                                ErrorView.getBody("Could not fetch the feed, see client logs for more information."));
                    } else {
                        Scaffold.getInstance().rebuild(PostsView.getBody(feed).toArray(new JComponent[0]));
                    }
                } catch (Exception e) {
                    Scaffold.getInstance().rebuild(ErrorView.getBody(e.toString()));
                    e.printStackTrace();
                }
            }

        };

        worker.execute();
    }
}
