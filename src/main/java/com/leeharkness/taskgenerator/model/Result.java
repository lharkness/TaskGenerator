package com.leeharkness.taskgenerator.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the result of the user's response to a sequence item
 */
@Data
@Builder(setterPrefix = "with")
public class Result {

    private long timestamp;
    private Status status;

    /**
     * Possible value for the user's response to a sequence item
     */
    public enum Status {
        C,
        I
    }
}
