package de.tum.cit.dos.eist.backend.models;

import java.util.List;

public record GetFeedResponse(List<Post> posts, Boolean hasUserAlreadyPosted) {
}
