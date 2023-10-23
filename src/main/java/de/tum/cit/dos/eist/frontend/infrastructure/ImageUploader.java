package de.tum.cit.dos.eist.frontend.infrastructure;

import java.io.File;
import java.net.URI;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class ImageUploader {

    private static final String BUCKET_NAME = "images";
    // LocalStack URL, change if your LocalStack is running on a different port or
    // host
    private static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4566";

    public void uploadImage(File file, String objectKey) throws Exception {
        AwsBasicCredentials localStackCredentials = AwsBasicCredentials.create("localstack", "localstack");

        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create(LOCALSTACK_S3_ENDPOINT))
                .region(Region.of("us-east-1")) // LocalStack doesn't care about the region for local testing
                .credentialsProvider(StaticCredentialsProvider.create(localStackCredentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                // Use ApacheHttpClient to avoid issues with async client in LocalStack
                .httpClientBuilder(ApacheHttpClient.builder())
                .overrideConfiguration(o -> o.addExecutionInterceptor(new LocalStackExecutionInterceptor()))
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .build();

        try {
            s3.putObject(putObjectRequest, RequestBody.fromFile(file.toPath()));
            System.out.println("Image uploaded successfully to LocalStack.");
        } finally {
            s3.close();
        }
    }

    // A simple execution interceptor to print out the request for debugging
    // purposes
    private static class LocalStackExecutionInterceptor
            implements software.amazon.awssdk.core.interceptor.ExecutionInterceptor {
        @Override
        public void afterMarshalling(Context.AfterMarshalling context, ExecutionAttributes executionAttributes) {
            System.out.println("Request: " + context.httpRequest());
        }
    }
}
