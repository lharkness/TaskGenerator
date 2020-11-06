package com.leeharkness.taskgenerator;

import com.leeharkness.taskgenerator.model.SequenceItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
public class SystemProperties {
    private List<SequenceItem> sequence;
    private String rule;
    private String correctResults;
    private String awsKeyId;
    private String awsKey;
    private String s3Url;

    public static SystemProperties loadFrom(Properties props) {
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setSequence(makeSequenceFrom(props.getProperty(SystemPropertyNames.SEQUENCE)));
        systemProperties.setRule(props.getProperty(SystemPropertyNames.RULE));
        systemProperties.setCorrectResults(props.getProperty(SystemPropertyNames.CORRECT_RESULTS));

        String awsKeyId = props.getProperty(SystemPropertyNames.AWS_KEY_ID);
        String awsKey = props.getProperty(SystemPropertyNames.AWS_KEY);
        String s3Url = props.getProperty(SystemPropertyNames.S3_URL);

        if (awsKeyId != null && awsKey != null && s3Url != null) {
            systemProperties.setAwsKeyId(awsKeyId);
            systemProperties.setAwsKey(awsKey);
            systemProperties.setS3Url(s3Url);
        }

        return systemProperties;
    }

    private static List<SequenceItem> makeSequenceFrom(String property) {
        String[] sequenceValues = property.split(",");
        List<SequenceItem> sequenceItems = new ArrayList<>();
        for (String sequenceValue : sequenceValues) {
            sequenceItems.add(SequenceItem.builder().withValue(sequenceValue).build());
        }
        return sequenceItems;
    }
}
