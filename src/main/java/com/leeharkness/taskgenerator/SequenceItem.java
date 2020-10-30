package com.leeharkness.taskgenerator;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a single item in the sequence to be displayed during the task
 */
@Data
@Builder(setterPrefix = "with")
public class SequenceItem {
    private String value;
}
