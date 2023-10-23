package de.tum.cit.dos.eist.frontend.controllers;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tum.cit.dos.eist.frontend.models.Feed;
import de.tum.cit.dos.eist.frontend.models.Post;
import de.tum.cit.dos.eist.frontend.models.User;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

public class FetchFeedController {
    private static FetchFeedController instance;

    private FetchFeedController() {
    }

    public static FetchFeedController getInstance() {
        if (instance == null) {
            instance = new FetchFeedController();
        }
        return instance;
    }

    public Feed fetchPosts() throws JsonParseException, JsonMappingException, IOException {
        AwsBasicCredentials localStackCredentials = AwsBasicCredentials.create("localstack", "localstack");

        // Configure the client to use the LocalStack
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        LambdaClient awsLambda = LambdaClient.builder()
                .httpClient(httpClient)
                .endpointOverride(URI.create("http://localhost:4566")) // Replace with your LocalStack Lambda endpoint
                .region(Region.of("us-east-1")) // Replace with the LocalStack region
                .credentialsProvider(StaticCredentialsProvider.create(localStackCredentials))
                .build();

        // Convert the query parameters to JSON
        String jsonPayload = "{"
                + "\"queryStringParameters\": {"
                + "\"userId\": \"student\""
                + "}"
                + "}";

        // Invoke the Lambda function
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName("get-feed")
                .payload(SdkBytes.fromUtf8String(jsonPayload))
                .build();
        InvokeResponse invokeResponse = awsLambda.invoke(invokeRequest);

        // Check the response
        if (invokeResponse.statusCode() == 200) {
            // Parse the JSON response into the Feed object
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = new String(invokeResponse.payload().asByteArray(), StandardCharsets.UTF_8);

            // Error messages are still
            if (jsonString.startsWith("{\"errorMessage")) {
                System.out.println("AWS Lambda failed with: " + jsonPayload + " as input. Response was: " + jsonString);
                throw new RuntimeException(jsonString);
            }

            Map<String, Object> responseMap = objectMapper.readValue(jsonString,
                    new TypeReference<Map<String, Object>>() {
                    });

            if (responseMap == null) {
                System.out.println("AWS Lambda from get-feed return null with " + jsonPayload
                        + " as input. Response was: " + jsonString);
                throw new RuntimeException(
                        "The get-feed function returned null. Make sure that you return a valid GetFeedResponse object and that you deploy the function correctly.");
            }

            // Extract posts and hasUserAlreadyPosted from response
            List<Map<String, Object>> postsDto = (List<Map<String, Object>>) responseMap.get("posts");

            System.out.println("Received the following response from the 'get-feed' function: " + postsDto);

            List<Post> posts = postsDto.stream()
                    .map(post -> parseUser(post))
                    .collect(Collectors.toList());

            boolean hasUserAlreadyPosted = (boolean) responseMap.get("hasUserAlreadyPosted");

            // Return the Feed object
            return new Feed(posts, hasUserAlreadyPosted);
        } else {
            // Handle the error response
            System.err.println("Error invoking Lambda function: " + invokeResponse.functionError());
        }

        // Return null or throw an exception if the function invocation failed
        return null;
    }

    private Post parseUser(Map<String, Object> post) {
        String displayName = (String) post.get("displayName");
        String userId = (String) post.get("userId");
        String presignedImageUrl = (String) post.get("presignedImageUrl");

        if (displayName == null) {
            System.out.println("The display name of the user is missing in the post. The post was: " + post);
            throw new RuntimeException("The display name of the user is missing in the post.");
        }

        if (userId == null) {
            System.out.println("The user ID is missing in the post. The post was: " + post);
            throw new RuntimeException("The user ID is missing in the post.");
        }

        if (presignedImageUrl == null) {
            System.out.println("The presigned image URL is missing in the post. The post was: " + post);
            throw new RuntimeException("The presigned image URL is missing in the post.");
        }

        return new Post(
                new User(displayName, userId),
                "Munich, Germany",
                presignedImageUrl);
    }
}
