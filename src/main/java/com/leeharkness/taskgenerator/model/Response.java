package com.leeharkness.taskgenerator.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a user response in an experiment.  Contains a value and timestamp that the response was created
 */
@Data
@Builder(setterPrefix = "with")
public class Response {
    private String value;
    private long timeStamp;
}
