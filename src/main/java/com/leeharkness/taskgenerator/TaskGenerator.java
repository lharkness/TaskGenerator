package com.leeharkness.taskgenerator;

import com.leeharkness.taskgenerator.aws.S3Accessor;
import com.leeharkness.taskgenerator.model.ExpectedResult;
import com.leeharkness.taskgenerator.model.ResponseValidator;
import com.leeharkness.taskgenerator.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.inject.Inject;
import java.io.StringWriter;
import java.util.List;

@Slf4j
public class TaskGenerator {

    private final S3Accessor s3Accessor;

    @Inject
    public TaskGenerator(S3Accessor s3Accessor) {
        this.s3Accessor = s3Accessor;
    }

    public void run(SystemProperties systemProperties, List<ExpectedResult> expectedResults) {
        ResponseValidator responseValidator = new ResponseValidator(systemProperties.getSequence(),
                systemProperties.getRule());

        // Validate behavior
        boolean kosher = true;
        for (ExpectedResult expectedResult : expectedResults) {
            List<Result> results = responseValidator.validate(expectedResult.getResponses());
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).getStatus() != expectedResult.getStatuses().get(i)) {
                    kosher = false;
                    log.error("Error!  Expected: " + expectedResult.getStatuses().get(i) +
                            " but received " + results.get(i).getStatus());
                }
            }
        }

        if (!kosher) {
            return;
        }

        // Generate Javascript
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        Template t = velocityEngine.getTemplate("taskArtifact.vm");

        VelocityContext context = new VelocityContext();
        context.put("items", systemProperties.getSequence());
        context.put("interval", 2000);
        context.put("lastItem", systemProperties.getSequence().size() + 1);
        context.put("rule", "Click when you see " + systemProperties.getRule());
        context.put("correctResults", systemProperties.getCorrectResults());

        StringWriter writer = new StringWriter();
        t.merge( context, writer );

        String htmlString = writer.toString();

        if (systemProperties.getAwsKeyId() != null && systemProperties.getAwsKey() != null &&
                systemProperties.getS3Url() != null) {
            s3Accessor.publishExercise(systemProperties.getAwsKeyId(), systemProperties.getAwsKey(),
                    systemProperties.getS3Url(), htmlString);
        }
        else {
            System.out.println(htmlString);
            log.info(htmlString);
        }

    }

}
