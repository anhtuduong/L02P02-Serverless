package de.tum.cit.dos.eist.backend.infrastructure;

import de.tum.cit.dos.eist.backend.models.User;

/**
 * FakeAwsSns is a fake implementation of the AWS SNS service.
 *
 * We use this class to avoid setting up the AWS SNS service to simplify this
 * exercise.
 */
public class FakeAwsSns {
    public void sendPushNotification(User user, String message) {
        // Just print the message to the console instead of sending a push
        // notification, see comment above.
        System.out.println("Sending push notification to " + user.displayName() + " with the title: '" + message + "'");
    }
}
