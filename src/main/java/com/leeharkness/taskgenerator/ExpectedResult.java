package com.leeharkness.taskgenerator;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class ExpectedResult {
    private List<Result.Status> statuses;
    private List<Response> responses;
}
