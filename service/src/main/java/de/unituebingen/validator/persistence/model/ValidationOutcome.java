package de.unituebingen.validator.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of a (validation) process or operation exceution status result
 *
 */
public enum ValidationOutcome {
	@JsonProperty("notValid")
	NOT_VALID("notValid"),
	@JsonProperty("valid")
	VALID("valid");
	
	private String outcome;

	private ValidationOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	
	
	
}
