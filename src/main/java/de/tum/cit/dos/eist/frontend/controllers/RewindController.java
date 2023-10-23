package de.tum.cit.dos.eist.frontend.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;

public class RewindController {
    private static RewindController instance;

    private AmazonS3 s3;
    private AmazonDynamoDB dynamoDB;

    private static final String BUCKET_NAME = "images";
    // LocalStack URL, change if your LocalStack is running on a different port or
    // host
    private static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4566";

    private RewindController() {
        this.s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(LOCALSTACK_S3_ENDPOINT, "us-east-1"))
                .withPathStyleAccessEnabled(true)
                .build();

        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(LOCALSTACK_S3_ENDPOINT, "us-east-1"))
                .build();
    }

    public static RewindController getInstance() {
        if (instance == null) {
            instance = new RewindController();
        }
        return instance;
    }

    public void rewind() {
        // Upload unblurred_images to S3
        uploadImage("images/user1-yesterday.jpg", "unblurred_images/1.jpg");
        uploadImage("images/user2-yesterday.jpg", "unblurred_images/2.jpg");
        uploadImage("images/student-yesterday.jpg", "unblurred_images/student.jpg");

        // Upload blurred_images to S3
        uploadImage("images/blurred-user1.jpg", "blurred_images/1.jpg");
        uploadImage("images/blurred-user2.jpg", "blurred_images/2.jpg");

        // Update DynamoDB
        setHasPostedToday("student", true);
        setHasPostedToday("1", true);
        setHasPostedToday("2", true);
    }

    private void uploadImage(String sourceKey, String destinationKey) {
        File file = new File(BeUnrealApp.class.getResource("/" + sourceKey).getFile());
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, destinationKey, file);
        s3.putObject(putObjectRequest);
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
