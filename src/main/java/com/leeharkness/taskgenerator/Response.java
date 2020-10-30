package com.leeharkness.taskgenerator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class Response {
    private String value;
    private long timeStamp;
}
