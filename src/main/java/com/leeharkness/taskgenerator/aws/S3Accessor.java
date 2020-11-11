package com.leeharkness.taskgenerator.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringInputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Represents a connection to S3
 */
@Slf4j
public class S3Accessor {

    /**
     * Publishes a given HTML string to the bucket
     * @param awsKeyId The AWS Key Id
     * @param awsKey The AWS Key
     * @param awsRegion The AWS region
     * @param bucketName the Bucket Name
     * @param fileName the File Name
     * @param htmlString the HTML String to publish
     */
    public void publishExercise(String awsKeyId, String awsKey, String awsRegion, String bucketName, String fileName,
                                String htmlString) throws UnsupportedEncodingException {
        // Publish htmlString to s3
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsKeyId, awsKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.fromName(awsRegion))
                .build();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/html");
        metadata.setContentLength(htmlString.length());

        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, new StringInputStream(htmlString),
                metadata);

        s3Client.putObject(request);
    }
}