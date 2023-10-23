package de.tum.cit.dos.eist.frontend.controllers;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;
import de.tum.cit.dos.eist.frontend.infrastructure.ImageUploader;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

public class SimulateController {
    private static SimulateController instance;

    private AmazonDynamoDB dynamoDB;

    // LocalStack URL, change if your LocalStack is running on a different port or
    // host
    private static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4566";

    private SimulateController() {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(LOCALSTACK_S3_ENDPOINT, "us-east-1"))
                .build();
    }

    public static SimulateController getInstance() {
        if (instance == null) {
            instance = new SimulateController();
        }
        return instance;
    }

    public void simulate() {
        callBeUnrealTimeFunction();
        simulateFriendsPost();
    }

    private void callBeUnrealTimeFunction() {
        AwsBasicCredentials localStackCredentials = AwsBasicCredentials.create("localstack", "localstack");

        // Configure the client to use the LocalStack
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        LambdaClient awsLambda = LambdaClient.builder()
                .httpClient(httpClient)
                .endpointOverride(URI.create(LOCALSTACK_S3_ENDPOINT)) // Replace with your LocalStack Lambda endpoint
                .region(Region.of("us-east-1")) // Replace with the LocalStack region
                .credentialsProvider(StaticCredentialsProvider.create(localStackCredentials))
                .build();

        // Invoke the Lambda function
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName("beunreal-time")
                .build();
        InvokeResponse invokeResponse = awsLambda.invoke(invokeRequest);

        // Check the response
        if (invokeResponse.statusCode() == 200) {
            String jsonString = invokeResponse.payload().asUtf8String();

            // Error messages are still
            if (jsonString.startsWith("{\"errorMessage")) {
                System.out.println("AWS Lambda failed with: " + jsonString + " as input. Response was: " + jsonString);
                throw new RuntimeException("Error invoking Lambda 'beunreal-time' function: " + jsonString);
            }
        } else {
            System.err.println("Error invoking Lambda function: " + invokeResponse.functionError());
        }
    }

    private void simulateFriendsPost() {
        System.out.println("Simulating that the friends are posting...");

        ImageUploader imageUploader = new ImageUploader();
        try {
            imageUploader.uploadImage(new File(BeUnrealApp.class.getResource("/images/user1.jpg").toURI()), "unblurred_images/1.jpg");
            imageUploader.uploadImage(new File(BeUnrealApp.class.getResource("/images/user2.jpg").toURI()), "unblurred_images/2.jpg");
            imageUploader.uploadImage(new File(BeUnrealApp.class.getResource("/images/blurred-user1.jpg").toURI()), "blurred_images/1.jpg");
            imageUploader.uploadImage(new File(BeUnrealApp.class.getResource("/images/blurred-user2.jpg").toURI()), "blurred_images/2.jpg");
            setHasPostedToday("1", true);
            setHasPostedToday("2", true);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void setHasPostedToday(String userId, boolean hasPosted) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(userId));

        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":val", new AttributeValue().withBOOL(hasPosted));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("users")
                .withKey(key)
                .withUpdateExpression("set hasPostedToday = :val")
                .withExpressionAttributeValues(attributeValues);

        dynamoDB.updateItem(updateItemRequest);
    }
}
