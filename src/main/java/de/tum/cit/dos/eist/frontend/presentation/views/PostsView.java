package de.tum.cit.dos.eist.frontend.presentation.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import de.tum.cit.dos.eist.frontend.models.Feed;
import de.tum.cit.dos.eist.frontend.models.Post;
import de.tum.cit.dos.eist.frontend.presentation.components.PostPanel;

public class PostsView {
    public static List<JComponent> getBody(Feed feed) {
        List<JComponent> postPanels = new ArrayList<JComponent>();
        for (Post post : feed.posts()) {
            postPanels.add(new PostPanel(post, feed.hasUserAlreadyPosted()));
        }
        return postPanels;
    }
}
