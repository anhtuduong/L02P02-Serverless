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
            deleteImage(user.uid(), FileStorage.BLURRED_IMAGES_FOLDER);
            deleteImage(user.uid(), FileStorage.UNBLURRED_IMAGES_FOLDER);
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }

    private void deleteImage(String userId, String folderName) {
        String objectKey = folderName + "/" + userId + ".jpg";
        fileStorage.deleteFile(objectKey);
    }
}