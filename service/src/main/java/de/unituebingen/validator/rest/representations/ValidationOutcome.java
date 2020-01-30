package de.unituebingen.validator.rest.representations;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the outcome of the validation process.
 *
 */
public enum ValidationOutcome {

	@JsonProperty("valid")
	VALID("valid"), @JsonProperty("notValid")
	NOT_VALID("notValid");

	String value;

	private ValidationOutcome(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
