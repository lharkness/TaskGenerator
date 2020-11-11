package com.leeharkness.taskgenerator.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * This thing will validate a user's responses to a sequence given a rule and
 * return that as a list of Results.
 * TODO: add the ability to evaluate a given rule rather than what this thing does, which depends on being given
 * the correct responses and then comparing the responses to those.
 */
@Slf4j
public class ResponseValidator {
	private final String rule;
	private final List<SequenceItem> sequence;

	/**
	 * Initialization ctor
	 * @param sequence The sequence of items
	 * @param rule The rule for when to push the button
	 */
	public ResponseValidator(List<SequenceItem> sequence, String rule) {
		this.sequence = sequence;
		this.rule = rule;
	}

	/**
	 * Produces a result list composed of the results of the user's responses to the given sequence evaluated via the
	 * given rule
	 * @param responses The responses to validate
	 * @return a list of Results describing how the supplied responses fared against the sequence and the rule
	 */
	public List<Result> validate(List<Response> responses) {
		List<Response> correctResponses = generateCorrectResponses();
		List<Result> results = new ArrayList<>();
		if (responses.size() != sequence.size()) {
			log.error("Invalid number of responses");
		}

		for (int i = 0; i < responses.size(); i++) {
			if (responses.get(i).getValue().trim().equals(correctResponses.get(i).getValue().trim())) {
				results.add(Result.builder().withStatus(Result.Status.C).build());
			}
			else {
				results.add(Result.builder().withStatus(Result.Status.I).build());
			}
		}

		return results;

	}

	/**
	 * Generates the correct responses from the given sequence and rule
 	 * @return the correct responses from the given sequence and rule
	 */
	private List<Response> generateCorrectResponses() {
		List<Response> correctResponses = new ArrayList<>();
		for (SequenceItem sequenceItem : sequence) {
			if (sequenceItem.getValue().trim().equals(rule)) {
				correctResponses.add(Response.builder().withValue("true").build());
			} else {
				correctResponses.add(Response.builder().withValue("false").build());
			}
		}

		return correctResponses;
	}
}
