package com.leeharkness.taskgenerator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.leeharkness.taskgenerator.model.ExpectedResult;
import com.leeharkness.taskgenerator.model.Response;
import com.leeharkness.taskgenerator.model.Result;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
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
 *    (optionally) the S3 Bucket Name.  Property name: S3_BUCKET_NAME
 *    (optionally) the S3 File Name.  Property name: S3_FILE_NAME
 *    (optionally) the S3 Region Name.  Property name: S3_REGION_NAME
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
			log.error("Error loading configuration data", ioe);
		}
	}

	/**
	 * Sets up main program logic
	 * @param args The command-line arguments
	 */
	private void run(String[] args) throws IOException {

    	List<ExpectedResult> expectedResults;

    	SystemProperties systemProperties;
    	// If the properties and expected results file paths are provided, read them
    	if (args.length == 2) {
			systemProperties = getSystemProperties(args[0]);
			expectedResults = readExpectedResults(new File(args[1]));

		}
    	else {
    		// Otherwise use the ones provided in the jar
			systemProperties = getDefaultSystemProperties();
    		expectedResults = readDefaultExpectedResults();
		}

		Injector injector = Guice.createInjector();
		TaskGenerator taskGenerator = injector.getInstance(TaskGenerator.class);
		taskGenerator.run(systemProperties, expectedResults);
	}

	/**
	 * Gets system properties from the jar
	 * @return the system properties found in the jar
	 */
	private SystemProperties getDefaultSystemProperties() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("setup.props");
		assert inputStream != null;
		return readSystemProperties(new InputStreamReader(inputStream));
	}

	/**
	 * Gets system properties from a file
	 * @param filePath The file which stores system properties
	 * @return the system properties found in the file
	 */
	private SystemProperties getSystemProperties(String filePath) throws IOException {
		InputStream inputStream = new FileInputStream(filePath);
		return readSystemProperties(new InputStreamReader(inputStream));
	}

	/**
	 * Read system properties from a file
	 * @param isr The input stream
	 * @return the SystemProperties found at filePath
	 */
	private SystemProperties readSystemProperties(InputStreamReader isr) throws IOException {
		Properties props = new Properties();
		props.load(isr);
		isr.close();
		return SystemProperties.loadFrom(props);
	}

	/**
	 * Reads the expected results from the jar (used for validation)
	 * @return the Expected Results from the Jar
	 */
	private List<ExpectedResult> readDefaultExpectedResults() {
		List<String> fileContents = getFileContentsFor("testData.txt");
		return getExpectedResults(fileContents);
	}

	/**
	 * Reads the expected results from a file (used for validation)
	 * @param file the file to get expected results from
	 * @return the expected results found in the file
	 */
	private List<ExpectedResult> readExpectedResults(File file) {
		List<String> fileContents = getFileContentsFor(file);
		return getExpectedResults(fileContents);
	}

	/**
	 * Converts file contents to expected results
	 * @param fileContents the List Strings found in the expected results file
	 * @return the expected results
	 */
	private List<ExpectedResult> getExpectedResults(List<String> fileContents) {
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

	/**
	 * Reads a file (assumes a text file - returns a List of lines)
	 * @param filePath the file to read
	 * @return the file contents
	 */
	private List<String> getFileContentsFor(File filePath) {
		List<String> lines = new ArrayList<>();
		try {
			InputStream inputStream = new FileInputStream(filePath);
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

	/**
	 * Reads a file (assumes a text file - returns a List of lines)
	 * @param filePath - the name of a file on the Classpath to read
	 * @return the Contents of the file
	 */
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
