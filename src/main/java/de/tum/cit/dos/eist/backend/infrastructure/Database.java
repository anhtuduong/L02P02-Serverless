package de.tum.cit.dos.eist.backend.infrastructure;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class Database {
    AmazonDynamoDB amazonDynamoDB;

    public Database() {
        this.amazonDynamoDB = initDynamoDbClient();
    }

    private AmazonDynamoDB initDynamoDbClient() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(AwsUtils.getCredentialsProvider())
                .withEndpointConfiguration(AwsUtils.getEndpointConfiguration())
                .build();
    }

    public ScanResult scan(ScanRequest request) {
        return amazonDynamoDB.scan(request);
    }

    public UpdateItemResult updateItem(UpdateItemRequest request) {
        return amazonDynamoDB.updateItem(request);
    }

    public GetItemResult getItem(GetItemRequest request) {
        return amazonDynamoDB.getItem(request);
    }
}
