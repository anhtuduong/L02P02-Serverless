package de.tum.cit.dos.eist.backend.functions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import de.tum.cit.dos.eist.backend.infrastructure.BeUnrealRepository;
import de.tum.cit.dos.eist.backend.infrastructure.FileStorage;
import de.tum.cit.dos.eist.backend.models.GetFeedResponse;
import de.tum.cit.dos.eist.backend.models.Post;
import de.tum.cit.dos.eist.backend.models.User;

public class GetFeedFunction implements RequestHandler<APIGatewayProxyRequestEvent, GetFeedResponse> {
    private BeUnrealRepository repository;
    private FileStorage fileStorage;

    public GetFeedFunction() {
        repository = new BeUnrealRepository();
        fileStorage = new FileStorage();
    }

    public GetFeedResponse handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        // First, we need read the userId from the request. This is the user
        // that calls the function. We ignore authentication for now.
        String userId = request.getQueryStringParameters().get("userId");

        // Then, we need to get the user from the repository to check if the
        // user has posted today.
        User user = repository.getUser(userId);

        // After getting the user status, we can get the posts for the feed.
        List<Post> posts = getPosts(user);

        // The posts need to be sorted before returning them.
        sortFeed(user.uid(), posts);

        // Finally, we return the posts and the hasPostedToday flag. The
        // frontend will use this flag to decide if the user can post again.
        return new GetFeedResponse(posts, user.hasPostedToday());
    }

    private List<Post> getPosts(User requester) {

        boolean hasUserPosted = requester.hasPostedToday();
        List<Post> posts = new ArrayList<>();

        // Add posts from friends who posted today
        List<User> friends = repository.getFriends(requester.uid());
        for (User friend : friends) {
            if (friend.hasPostedToday()) {
                // The image path depends on whether the user has posted or not.
                String key = getImagePath(friend.uid(), hasUserPosted);
                // We need to generate a presigned URL for the image so that the
                // frontend can access the image.
                String presignedUrl = fileStorage.generatePresignedUrl(FileStorage.IMAGES_BUCKET, key);
                posts.add(new Post(friend.uid(), friend.displayName(), presignedUrl));
            }
        }

        // Add post from user if the user posted today
        if (hasUserPosted) {
            // The image path depends on whether the user has posted or not.
            String key = getImagePath(requester.uid(), true);
            // We need to generate a presigned URL for the image so that the
            // frontend can access the image.
            String presignedUrl = fileStorage.generatePresignedUrl(FileStorage.IMAGES_BUCKET, key);
            // Add the post of the requester at the end of the post list
            posts.add(new Post(requester.uid(), requester.displayName(), presignedUrl));
        }

        return posts;
    }

    private void sortFeed(String userId, List<Post> posts) {
        // If user posted today, the post is at the end of the list
        Post lastPost = posts.get(posts.size() - 1);
        boolean hasUserPosted = false;
        if (lastPost.userId().equals(userId)) {
            hasUserPosted = true;
            // Remove the user's post out of list
            // so the list can be sorted without the user's post
            posts.remove(lastPost);
        }

        // Sort the list by field displayName
        posts.sort(Comparator.comparing(Post::displayName));

        // If user posted today, add user's post at the start of the list
        if (hasUserPosted) {
            posts.add(0, lastPost);
        }
    }

    private String getImagePath(String userId, boolean hasUserPosted) {
        // Decide which image folder to use from FileStorage
        String folderName = null;
        if (hasUserPosted) {
            folderName = FileStorage.UNBLURRED_IMAGES_FOLDER;
        } else {
            folderName = FileStorage.BLURRED_IMAGES_FOLDER;
        }
        return folderName + "/" + userId + ".jpg";
    }
}
