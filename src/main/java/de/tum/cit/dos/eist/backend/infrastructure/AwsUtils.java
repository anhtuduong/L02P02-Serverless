package de.tum.cit.dos.eist.backend.infrastructure;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;

public class AwsUtils {
    /**
     * The AWS region to use.
     *
     * We are using the US East (N. Virginia) region because it is the default
     * region for LocalStack and therefore you don't need to change anything
     * when using the web interface.
     */
    public static final Regions REGION = Regions.US_EAST_1;

    private static final String ACCESS_KEY = "localstack";
    private static final String SECRET_KEY = "localstack";

    /**
     * The host URL for the local AWS environment when running side LocalStack.
     *
     * In case you are executing the code on your local machine and not inside
     * LocalStack, you probably want to use the following URL:
     * http://localhost:4566
     */
    private static final String HOST = "http://localhost.localstack.cloud:4566";

    public static EndpointConfiguration getEndpointConfiguration() {
        return new EndpointConfiguration(HOST, AwsUtils.REGION.getName());
    }

    public static AWSStaticCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
    }

    private static BasicAWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
    }
}