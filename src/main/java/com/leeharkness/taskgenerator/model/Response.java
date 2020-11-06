package com.leeharkness.taskgenerator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class Response {
    private String value;
    private long timeStamp;
}
