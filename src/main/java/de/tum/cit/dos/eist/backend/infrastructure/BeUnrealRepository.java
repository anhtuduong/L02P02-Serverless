package de.tum.cit.dos.eist.backend.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import de.tum.cit.dos.eist.backend.models.User;

public class BeUnrealRepository {
    public static final String DYNAMODB_TABLE_NAME = "users";

    private Database database;

    public BeUnrealRepository() {}
    
    /**
     * Initializes the database.
     *
     * Should be private, but is public because it is required for testing.
     */
    public void initDatabase() {
        this.database = new Database();
    }

    /**
     * Ensures that the the database is initialized.
     *
     * Normally, the database is initialized in the constructor. However, due to
     * testing security policies by Artemis, the S3 client cannot be initialized
     * in the constructor. Therefore, we need to initialize it lazily.
     */
    private void ensureDatabaseIsInitialized() {
        if (database == null) {
            initDatabase();
        }
    }

    /**
     * Updates the hasPostedToday field for the given user ID in the DynamoDB
     * table.
     * 
     * The hasPostedToday field indicates whether the user has posted a picture
     * today.
     *
     * @param userId         the ID of the user to update
     * @param hasPostedToday the new value for the hasPostedToday field
     */
    public void updateHasPostedToday(String userId, boolean hasPostedToday) {
        ensureDatabaseIsInitialized();

        // Key to identify the item
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(userId));

        // Update Expression
        String updateExpression = "SET hasPostedToday = :newValue";

        // Expression Attribute Values
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newValue", new AttributeValue().withBOOL(hasPostedToday));

        // Create an UpdateItemRequest
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(DYNAMODB_TABLE_NAME)
                .withKey(key)
                .withUpdateExpression(updateExpression)
                .withExpressionAttributeValues(expressionAttributeValues);

        // Update the item
        database.updateItem(updateItemRequest);

        System.out.println("Item updated successfully!");
    }

    /**
     * Retrieves a user from the DynamoDB table.
     *
     * @param userId the ID of the user to retrieve
     * @return a User object representing the user
     */
    public User getUser(String userId) {
        ensureDatabaseIsInitialized();

        AttributeValue attributeValKey = new AttributeValue().withS(userId);
        GetItemRequest request = new GetItemRequest().addKeyEntry("id", attributeValKey)
                .withTableName(DYNAMODB_TABLE_NAME);
        GetItemResult dynamoDbResponse = database.getItem(request);

        String name = dynamoDbResponse.getItem().get("displayName").getS();
        boolean hasPostedToday = dynamoDbResponse.getItem().get("hasPostedToday").getBOOL();

        return new User(
                userId,
                name,
                hasPostedToday);
    }

    /**
     * Retrieves all users from the DynamoDB table.
     *
     * @return a list of User objects representing all users in the table
     */
    public List<User> getAllUsers() {
        ensureDatabaseIsInitialized();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DYNAMODB_TABLE_NAME);

        ScanResult scanResult = database.scan(scanRequest);

        List<User> users = new ArrayList<>();

        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            users.add(
                    new User(
                            item.get("id").getS(),
                            item.get("displayName").getS(),
                            item.get("hasPostedToday").getBOOL()));
        }

        return users;
    }

    /**
     * Retrieves a list of friends for the given user ID from the DynamoDB table.
     *
     * @param userId the ID of the user whose friends to retrieve
     * @return a list of User objects representing the user's friends
     */
    public List<User> getFriends(String userId) {
        ensureDatabaseIsInitialized();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DYNAMODB_TABLE_NAME)
                .withFilterExpression("#id <> :value")
                .withExpressionAttributeNames(
                        Map.of("#id", "id"))
                .withExpressionAttributeValues(
                        Map.of(":value", new AttributeValue(userId)));

        ScanResult scanResult = database.scan(scanRequest);

        List<User> friends = new ArrayList<>();

        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            String id = item.get("id").getS();
            String name = item.get("displayName").getS();
            boolean hasPostedToday = item.get("hasPostedToday").getBOOL();

            friends.add(new User(id, name, hasPostedToday));
        }

        return friends;
    }
}
