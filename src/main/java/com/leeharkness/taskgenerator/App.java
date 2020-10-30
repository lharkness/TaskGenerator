package com.leeharkness.taskgenerator;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Driver application for task generation.  This thing will take
 *   a file containing
 *    the allowable sequence items
 *    the sequence for the exercise
 *    the rule
 *
 *  a file containing
 *    a set of pairs of lines
 *      the first line is a sample input
 *      the second line is expected output
 *
 *  This program will verify that the actual output given the rule is the expected output
 */
@Slf4j
public class App {
	/**
	 * Main program entry point.
	 * @param args Expects two strings - the file path to the setup and the file path to the test data
	 */
    public static void main(String[] args) {
	    // Read our inputs - for now, let's hardcode them
	    List<SequenceItem> sequence = Arrays.asList(
	    		SequenceItem.builder().withValue("Red").build(),
				SequenceItem.builder().withValue("Blue").build(),
				SequenceItem.builder().withValue("Red").build(),
				SequenceItem.builder().withValue("Orange").build(),
				SequenceItem.builder().withValue("Yellow").build()
		);
		// This should be in the setup file
	    String rule = "Red";

	    ResponseValidator responseValidator = new ResponseValidator(sequence, rule);

	    // These will be in the test data file
	    List<Response> rawValidResponse = generateValidResponse(sequence, rule);
	    List<Response> rawInvalidResponse = generateInvalidResponse(sequence, rule);

	    List<Result> validTaskResult = responseValidator.validate(rawValidResponse);
	    List<Result> invalidTaskResult = responseValidator.validate(rawInvalidResponse);

	    // Validate behavior
		System.out.println(validTaskResult.toString());
		System.out.println(invalidTaskResult.toString());

		// TODO: generate HIT, call MTS to publish to Dev Sandbox
    }

    static List<Response> generateValidResponse(List<SequenceItem> sequence, String rule) {
    	List<Response> responseList = new ArrayList<>();

    	// For now, we'll hardcode this to work with the rule we have above
		Response firstResponse = Response.builder().withTimeStamp(1L).withValue("true").build();
		Response secondResponse = Response.builder().withTimeStamp(2L).withValue("false").build();
		Response thirdResponse = Response.builder().withTimeStamp(3L).withValue("true").build();
		Response fourthResponse = Response.builder().withTimeStamp(4L).withValue("false").build();
		Response fifthResponse = Response.builder().withTimeStamp(5L).withValue("false").build();

		responseList.add(firstResponse);
		responseList.add(secondResponse);
		responseList.add(thirdResponse);
		responseList.add(fourthResponse);
		responseList.add(fifthResponse);

    	return responseList;
    }

    static List<Response> generateInvalidResponse(List<SequenceItem> sequence, String rule) {
		List<Response> responseList = new ArrayList<>();

		// For now, we'll hardcode this to work with the rule we have above
		Response firstResponse = Response.builder().withTimeStamp(1L).withValue("true").build();
		Response secondResponse = Response.builder().withTimeStamp(2L).withValue("false").build();
		Response thirdResponse = Response.builder().withTimeStamp(3L).withValue("false").build();
		Response fourthResponse = Response.builder().withTimeStamp(4L).withValue("false").build();
		Response fifthResponse = Response.builder().withTimeStamp(5L).withValue("false").build();

		responseList.add(firstResponse);
		responseList.add(secondResponse);
		responseList.add(thirdResponse);
		responseList.add(fourthResponse);
		responseList.add(fifthResponse);

		return responseList;
    }
}
