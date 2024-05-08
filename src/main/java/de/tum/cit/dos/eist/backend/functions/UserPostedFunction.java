package de.tum.cit.dos.eist.backend.functions;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

import de.tum.cit.dos.eist.backend.infrastructure.BeUnrealRepository;
import de.tum.cit.dos.eist.backend.infrastructure.BlurringService;
import de.tum.cit.dos.eist.backend.infrastructure.FakeAwsSns;
import de.tum.cit.dos.eist.backend.infrastructure.FileStorage;
import de.tum.cit.dos.eist.backend.models.User;

public class UserPostedFunction implements RequestHandler<S3Event, String> {
    private BeUnrealRepository repository;
    private FileStorage fileStorage;
    private FakeAwsSns awsSns;
    private BlurringService blurringService;

    public UserPostedFunction() {
        repository = new BeUnrealRepository();
        fileStorage = new FileStorage();
        awsSns = new FakeAwsSns();
        blurringService = new BlurringService();
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        String userId = getUserIdFromEvent(event);
        String userDisplayName = repository.getUser(userId).displayName();

        // The event contains the records that have been uploaded to the S3. In
        // theory, there could be multiple records in one event (user uploads
        // multiple images at once). But in our case, the user can only upload
        // one image at a time. Therefore, the for loop is only executed once.
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            // The key is the path to the image in the S3 bucket. It's needed
            // to get the image from the S3 bucket which triggered the event.
            String key = record.getS3().getObject().getKey();

            // Blur the image that triggered the S3 event

            // Update the blurred image in the S3 bucket
        }

        // Notify the user's friends that the user has posted
        // with the following message: "NAME has posted their BeUnreal."
        for (User user : repository.getAllUsers()) {
            String message = userDisplayName + " has posted their BeUnreal.";
            awsSns.sendPushNotification(user, message);
        }

        // Update the state of the user
        repository.updateHasPostedToday(userId, true);

        return "Image processing complete.";
    }

    private String getUserIdFromEvent(S3Event event) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            // We assume that users only upload one image at a time.
            return record.getS3().getObject().getKey().split("/")[1].split("\\.")[0];
        }
        return null;
    }
}
