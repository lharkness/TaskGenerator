package com.leeharkness.taskgenerator;

import com.leeharkness.taskgenerator.model.SequenceItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * All System properties
 */
@Data
public class SystemProperties {
    private List<SequenceItem> sequence;
    private String rule;
    private String correctResults;
    private String awsKeyId;
    private String awsKey;
    private String s3BucketName;
    private String s3FileName;
    private String s3Region;

    /**
     * Populates system properties from a Properties object
     * @param props The Properties
     * @return the SystemProperties found in the props param
     */
    public static SystemProperties loadFrom(Properties props) {
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setSequence(makeSequenceFrom(props.getProperty(SystemPropertyNames.SEQUENCE)));
        systemProperties.setRule(props.getProperty(SystemPropertyNames.RULE));
        systemProperties.setCorrectResults(props.getProperty(SystemPropertyNames.CORRECT_RESULTS));

        String awsKeyId = props.getProperty(SystemPropertyNames.AWS_KEY_ID);
        String awsKey = props.getProperty(SystemPropertyNames.AWS_KEY);
        String s3BucketName = props.getProperty(SystemPropertyNames.S3_BUCKET);
        String s3FileName = props.getProperty(SystemPropertyNames.S3_FILE_NAME);
        String s3Region = props.getProperty(SystemPropertyNames.S3_REGION);

        systemProperties.setAwsKeyId(awsKeyId);
        systemProperties.setAwsKey(awsKey);
        systemProperties.setS3BucketName(s3BucketName);
        systemProperties.setS3FileName(s3FileName);
        systemProperties.setS3Region(s3Region);

        return systemProperties;
    }

    /**
     * Used to determine if we have S3 info
     * @return true if we have all the S3 info we need, false if not
     */
    public boolean weHaveAllS3Properties() {
        return awsKey != null && awsKeyId != null && s3BucketName != null && s3FileName != null && s3Region != null;
    }

    /**
     * Creates a sequence (a list of SequenceItems) from a given String
     * @param property the String which contains the representation of the list of Sequence Items
     * @return the List of Sequence Items
     */
    private static List<SequenceItem> makeSequenceFrom(String property) {
        String[] sequenceValues = property.split(",");
        List<SequenceItem> sequenceItems = new ArrayList<>();
        for (String sequenceValue : sequenceValues) {
            sequenceItems.add(SequenceItem.builder().withValue(sequenceValue).build());
        }
        return sequenceItems;
    }
}
