package de.tum.cit.dos.eist.backend.functions;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import de.tum.cit.dos.eist.backend.infrastructure.BeUnrealRepository;
import de.tum.cit.dos.eist.backend.infrastructure.FakeAwsSns;
import de.tum.cit.dos.eist.backend.infrastructure.FileStorage;
import de.tum.cit.dos.eist.backend.models.User;

public class BeUnrealTimeFunction
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private BeUnrealRepository repository;
    private FileStorage fileStorage;
    private FakeAwsSns awsSns;

    public BeUnrealTimeFunction() {
        repository = new BeUnrealRepository();
        fileStorage = new FileStorage();
        awsSns = new FakeAwsSns();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        for (User user : repository.getAllUsers()) {
            // Send a push notification to each user with the message "Time to BeUnreal."
            awsSns.sendPushNotification(user, "Time to BeUnreal.");

            // Update the state of each user by reseting "hasPostedToday"
            repository.updateHasPostedToday(user.uid(), false);

            // Use the deleteImage method to delete the images
            deleteImage(user.uid(), FileStorage.IMAGES_BUCKET);
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }

    private void deleteImage(String userId, String folderName) {
        // Create a key to delete the blurred and unblurred images
        String blurredImageKey = folderName + "/" + FileStorage.BLURRED_IMAGES_FOLDER + "/" + userId + ".jpg";
        String unblurredImageKey = folderName + "/" + FileStorage.UNBLURRED_IMAGES_FOLDER + "/" + userId + ".jpg";

        // Delete both images
        fileStorage.deleteFile(blurredImageKey);
        fileStorage.deleteFile(unblurredImageKey);
    }
}