package com.leeharkness.taskgenerator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.leeharkness.taskgenerator.model.ExpectedResult;
import com.leeharkness.taskgenerator.model.Response;
import com.leeharkness.taskgenerator.model.Result;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Driver application for task generation.  This thing will take two optional arguments.  If one is present then they
 * both must be present.
 *   a property file containing
 *    the sequence for the exercise.  Property name: SEQUENCE
 *    the correct results for the exercise.  Property name: CORRECT_RESULTS
 *    the rule.  Property Name: RULE
 *    (optionally) the AWS key Id.  Property name: AWS_KEY_ID
 *    (optionally) the AWS key.  Property name: AWS_KEY
 *    (optionally) the S3 URL.  Property name: S3_URL
 *
 *  a file containing
 *    a set of pairs of lines
 *      the first line in the pair is a sample input
 *      the second line in the pair is expected output
 *
 *  This program will verify that the actual output given the rule is the expected output and then (optionally)
 *  update S3
 *
 *  TODO: log to a file rather than the console
 */
@Slf4j
public class App {

	/**
	 * Main program entry point.
	 * @param args Optionally provide two strings - the file path to the setup and the file path to the test data
	 */
    public static void main(String[] args) {
		App app = new App();
		try {
			app.run(args);
		}
		catch (IOException ioe) {
			log.error("Error loading properties", ioe);
		}
	}

	private void run(String[] args) throws IOException {

    	List<ExpectedResult> expectedResults;

    	SystemProperties systemProperties;
    	// If the properties and expected results file paths are provided, read them
    	if (args.length == 2) {
			systemProperties = readSystemProperties(args[0]);
			expectedResults = readExpectedResults(args[1]);

		}
    	else {
    		// Otherwise use the ones provided in the jar
			systemProperties = readSystemProperties("setup.props");
    		expectedResults = readExpectedResults("testData.txt");
		}

		Injector injector = Guice.createInjector();
		TaskGenerator taskGenerator = injector.getInstance(TaskGenerator.class);
		taskGenerator.run(systemProperties, expectedResults);
	}

	/**
	 * Read system properties from a file
	 * @param filePath The file path to the properties file
	 * @return the SystemProperties found at filePath
	 */
	private SystemProperties readSystemProperties(String filePath) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(filePath);
		assert inputStream != null;
		InputStreamReader isr = new InputStreamReader(inputStream);
		Properties props = new Properties();
		props.load(isr);

		return SystemProperties.loadFrom(props);

	}

	private List<ExpectedResult> readExpectedResults(String fileName) {
		List<String> fileContents = getFileContentsFor(fileName);
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
			// The second line is the expected results for those responses
			String[] resultValues = fileContents.get(i).split(",");
			List<Result.Status> statuses = new ArrayList<>();
			for (String resultValue : resultValues) {
				statuses.add(Result.Status.valueOf(resultValue.trim()));
			}
			expectedResults.add(ExpectedResult.builder().withResponses(responses).withStatuses(statuses).build());
		}
		return expectedResults;
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
