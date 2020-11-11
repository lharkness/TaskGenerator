package com.leeharkness.taskgenerator.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Represents an expected result for an experiment.  This is for integration purposes only.  In that context an
 * expected result is the set of results generated for an experiment given a set of responses.
 * Contains a list of statuses (e.g. correct and incorrect) and a list of Responses that generate those statuses
 */
@Data
@Builder(setterPrefix = "with")
public class ExpectedResult {
    private List<Result.Status> statuses;
    private List<Response> responses;
}
