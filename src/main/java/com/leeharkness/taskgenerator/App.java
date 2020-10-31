package com.leeharkness.taskgenerator;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
		App app = new App();
		app.run(args);
	}

	private void run(@SuppressWarnings("unused") String[] args) {
    	// Read our inputs - for now, get them from resources within this jar
		List<SequenceItem> sequence = readSequence();
	    String rule = readRule();
		List<ExpectedResult> expectedResults = readExpectedResults();

	    ResponseValidator responseValidator = new ResponseValidator(sequence, rule);

	    // Validate behavior
		for (ExpectedResult expectedResult : expectedResults) {
			List<Result> results = responseValidator.validate(expectedResult.getResponses());
			for (int i = 0; i < results.size(); i++) {
				if (results.get(i).getStatus() != expectedResult.getStatuses().get(i)) {
					System.out.println("Error!  Expected: " + expectedResult.getStatuses().get(i) +
							" but received " + results.get(i).getStatus());
				}
			}
		}

		// Generate Javascript
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		Template t = velocityEngine.getTemplate("taskArtifact.vm");

		VelocityContext context = new VelocityContext();
		context.put("items", sequence);
		context.put("interval", 2000);
		context.put("lastItem", sequence.size() + 1);
		context.put("rule", "Click when you see " + rule);

		StringWriter writer = new StringWriter();
		t.merge( context, writer );

		System.out.println(writer.toString());


		// TODO: generate HIT, call MTS to publish to Dev Sandbox -publish HTML to S3
    }

	private List<ExpectedResult> readExpectedResults() {
		List<String> fileContents = getFileContentsFor("testData.txt");
		if (fileContents.size() % 2 != 0) {
			log.error("Expected Results contains an odd number of lines");
		}
		List<ExpectedResult> expectedResults = new ArrayList<>();
		int i = -1;
		while (i < fileContents.size() - 1) {
			i++;
			// The first line is responses - absent real timing data
			String[] responseValues = fileContents.get(i).split(",");
			List<Response> responses = new ArrayList<>();
			for (String response : responseValues) {
				responses.add(Response.builder()
						.withTimeStamp(new Date().getTime())
						.withValue(response.trim())
						.build());
			}
			i++;
			// The second line is the expected results
			String[] resultValues = fileContents.get(i).split(",");
			List<Result.Status> statuses = new ArrayList<>();
			for (String resultValue : resultValues) {
				statuses.add(Result.Status.valueOf(resultValue.trim()));
			}
			expectedResults.add(ExpectedResult.builder().withResponses(responses).withStatuses(statuses).build());
		}
		return expectedResults;
	}

	private String readRule() {
		List<String> fileContents = getFileContentsFor("setup.txt");
		return fileContents.get(1).trim();
	}

	private List<SequenceItem> readSequence() {
		List<String> fileContents = getFileContentsFor("setup.txt");
		String[] sequenceValues = fileContents.get(0).split(",");
		List<SequenceItem> sequenceItems = new ArrayList<>();
		for (String sequenceValue : sequenceValues) {
			sequenceItems.add(SequenceItem.builder().withValue(sequenceValue).build());
		}
		return sequenceItems;
	}

	private List<String> getFileContentsFor(String filePath) {
		List<String> lines = new ArrayList<>();
    	try {
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream(filePath);
			assert inputStream != null;
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			lines = br.lines().collect(Collectors.toList());
			br.close();
		}
    	catch (IOException ioe) {
    		log.error(ioe.getLocalizedMessage(), ioe);
		}
		return lines;
	}
}
