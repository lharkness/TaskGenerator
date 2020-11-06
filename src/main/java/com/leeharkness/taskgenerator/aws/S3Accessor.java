package com.leeharkness.taskgenerator.aws;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Accessor {
    public void publishExercise(String awsKeyId, String awsKey, String s3Url, String htmlString) {
        // Publish htmlString to s3, update permissions to make it world readable
        log.info("Publishing to S3");
    }
}
