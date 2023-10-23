package de.tum.cit.dos.eist.frontend.models;

import java.util.List;

public record Feed(List<Post> posts, boolean hasUserAlreadyPosted) {

}
