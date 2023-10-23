package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;
import de.tum.cit.dos.eist.frontend.controllers.FetchFeedController;
import de.tum.cit.dos.eist.frontend.controllers.UploadController;
import de.tum.cit.dos.eist.frontend.models.Feed;
import de.tum.cit.dos.eist.frontend.presentation.views.ErrorView;
import de.tum.cit.dos.eist.frontend.presentation.views.LoadingView;
import de.tum.cit.dos.eist.frontend.presentation.views.PostsView;

public class NotPostedOverlay extends JPanel {
    public NotPostedOverlay() {
        JPanel overlay = this;
        overlay.setLayout(new GridBagLayout());
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 500, 500);

        // Change cursor to hand cursor when hovering over the overlay
        overlay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel iconLabel = new JLabel(new ImageIcon(BeUnrealApp.class.getResource("/images/hidden-icon.png")));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel text1 = new JLabel("Post to view");
        text1.setFont(new Font("Infer", Font.BOLD, 18));
        text1.setForeground(Color.WHITE);

        JLabel text2 = new JLabel("To view your friends' BeReal, share yours with them.");
        text2.setForeground(Color.WHITE);
        text2.setFont(new Font("Infer", Font.PLAIN, 14));
        text2.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        CustomContainer button = new CustomContainer("Post a BeUnreal.");

        overlay.add(iconLabel, gbc);
        overlay.add(text1, gbc);
        overlay.add(text2, gbc);
        overlay.add(button, gbc);

        overlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Overlay clicked!");

                File file = pickFile();
                if (file != null) {
                    Scaffold.getInstance().rebuild(LoadingView.getBody());

                    SwingWorker<Feed, Void> worker = new SwingWorker<Feed, Void>() {
                        @Override
                        protected Feed doInBackground() throws Exception {
                            System.out.println("Uploading image...");
                            UploadController.getInstance().uploadImage(file);

                            System.out.println("Waiting for 10 seconds to ensure 'beunreal-time' function finished execution...");
                            // We 10 seconds because it takes a few seconds until th
                            // AWS Lambda function gets triggered and update the
                            // user status in DynamoDB.
                            Thread.sleep(10_000);

                            System.out.println("Fetching posts again...");
                            FetchFeedController controller = FetchFeedController.getInstance();
                            return controller.fetchPosts();
                        }

                        @Override
                        protected void done() {
                            try {
                                Feed feed = get();
                                if (feed == null) {
                                    Scaffold.getInstance().rebuild(
                                            ErrorView.getBody(
                                                    "Could not fetch the feed, see client logs for more information."));
                                } else {
                                    Scaffold.getInstance()
                                            .rebuild(PostsView.getBody(feed).toArray(new JComponent[0]));
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
        });
    }

    private File pickFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Image files (*.jpg)";
            }
        });

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw a semi-transparent black rectangle to darken the background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 0.35f)); // Black with 35% opacity
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

class CustomContainer extends JPanel {
    private static final int ROUNDED_CORNER_RADIUS = 20;

    public CustomContainer(String text) {
        setOpaque(false);
        setLayout(null); // Using null layout for simplicity in this example

        JLabel label = new JLabel(text);
        label.setFont(new Font("Infer", Font.PLAIN, 18));
        label.setForeground(Color.BLACK); // Text color
        label.setBackground(new Color(255, 255, 255)); // White background

        Insets padding = new Insets(8, 16, 8, 16); // Top, left, bottom, right padding
        Dimension labelSize = label.getPreferredSize();
        label.setBounds(padding.left, padding.top, labelSize.width, labelSize.height);

        add(label);

        setPreferredSize(new Dimension(labelSize.width + padding.left + padding.right,
                labelSize.height + padding.top + padding.bottom));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ROUNDED_CORNER_RADIUS, ROUNDED_CORNER_RADIUS);
    }
}
