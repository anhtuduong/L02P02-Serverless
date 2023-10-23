package de.tum.cit.dos.eist.backend.infrastructure;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class FileStorage {
    public static final String IMAGES_BUCKET = "images";

    public static final String BLURRED_IMAGES_FOLDER = "blurred_images";
    public static final String UNBLURRED_IMAGES_FOLDER = "unblurred_images";

    private AmazonS3 s3Client;

    public FileStorage() {
    }

    /**
     * Initializes the S3 client.
     *
     * Should be private, but is public because it is required for testing.
     */
    public void initS3Client() {
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(AwsUtils.getEndpointConfiguration())
                .withPathStyleAccessEnabled(true)
                .build();
    }

    /**
     * Ensures that the S3 client is initialized.
     *
     * Normally, the S3 client is initialized in the constructor. However, due
     * to testing security policies by Artemis, the S3 client cannot be
     * initialized in the constructor. Therefore, we need to initialize it
     * lazily.
     */
    private void ensureS3ClientIsInitialized() {
        if (s3Client == null) {
            initS3Client();
        }
    }

    public void deleteFile(String objectKey) {
        ensureS3ClientIsInitialized();

        try {
            s3Client.deleteObject(IMAGES_BUCKET, objectKey);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    public BufferedImage getImageFile(String objectKey) throws IOException {
        ensureS3ClientIsInitialized();

        GetObjectRequest getObjectRequest = new GetObjectRequest(IMAGES_BUCKET, objectKey);
        S3Object s3Object = s3Client.getObject(getObjectRequest);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

        BufferedImage image = ImageIO.read(s3ObjectInputStream);
        s3ObjectInputStream.close();

        return image;
    }

    public String generatePresignedUrl(String bucketName, String key) {
        ensureS3ClientIsInitialized();

        // Generate the presigned URL. The presigned URL can be used by the
        // client to access and display the image.
        return s3Client.generatePresignedUrl(bucketName, key, null).toString();
    }

    public void uploadImageFile(BufferedImage image, String objectKey) throws IOException {
        ensureS3ClientIsInitialized();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(os.size());

        s3Client.putObject(IMAGES_BUCKET, objectKey, is, metadata);

        os.close();
        is.close();
    }
}
