package de.tum.cit.dos.eist.frontend.presentation.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tum.cit.dos.eist.frontend.presentation.components.Theme;

public class ErrorView {
    public static JPanel getBody(String message) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Theme.backgroundColor);

        String helpfulMessage = maybeAddHelpfulMessage(message);
        String labelText = "<html><div style='width:300px;text-align:center'><div>" + helpfulMessage + "</div><p></p>General tip: It can be very helpful to look at the LocalStack logs. The exercise description describes how to access the LocalStack logs.</p>" + "</div></html>";
        JLabel errorLabel = new JLabel(labelText);
        errorLabel.setForeground(Color.RED);

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(errorLabel);
        box.add(Box.createHorizontalGlue());

        errorPanel.add(box, BorderLayout.PAGE_START);
        return errorPanel;
    }

    static String maybeAddHelpfulMessage(String error) {
        if (error.contains("Connection refused")) {
            return "The backend is not running. Please make sure you started LocalStack. Error: " + error;
        }

        if (error.contains("function:get-feed (Service: Lambda, Status Code: 404")) {
            return "LocalStack is running, but the Lambda function 'get-feed' is not deployed. Take a look into the exercise description on how to deploy a function. Error: " + error;
        }

        if (error.contains("function:beunreal-time (Service: Lambda, Status Code: 404")) {
            return "LocalStack is running, but the Lambda function 'beunreal-time' is not deployed. Take a look into the exercise description on how to deploy a function. Error: " + error;
        }

        if (error.contains("Cannot do operations on a non-existent table (Service: AmazonDynamoDBv2; Status Code: 400; Error Code: ResourceNotFoundException; Request ID")) {
            return "LocalStack is running, but the DynamoDB table is not created. It seems like you have not run the setup script. Please run the setup script. See console output for more error details";
        }

        return error;
    }
}
