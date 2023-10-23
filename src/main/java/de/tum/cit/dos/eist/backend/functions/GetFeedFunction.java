package de.tum.cit.dos.eist.backend.functions;

import java.util.ArrayList;
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

        List<Post> posts = new ArrayList<>();

        /*When creating a post, we need a presigned URL.
        Incorporate the following code with the correct parameters for that.
         */
        // The image path depends on whether the user has posted or not.
        String key = getImagePath();
        // We need to generate a presigned URL for the image so that the
        // frontend can access the image.
        String presignedUrl = fileStorage.generatePresignedUrl(FileStorage.IMAGES_BUCKET, key);

        //TODO: Part 1 your code here
        return posts;
    }

    private void sortFeed(String userId, List<Post> posts) {
        //TODO: Part 1 Sort the list posts
    }

    private String getImagePath(String userId, boolean hasUserPosted) {
        // Decide which image folder to use from FileStorage
        String folderName = null;
        //TODO: Part 1 your code
        return folderName + "/" + userId + ".jpg";
    }
}
