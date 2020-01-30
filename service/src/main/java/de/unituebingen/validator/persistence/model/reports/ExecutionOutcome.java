package de.unituebingen.validator.persistence.model.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Outcome of tool execution
 * @author Fabian Hamm
 *
 */
public enum ExecutionOutcome {
	@JsonProperty("success")
	SUCCESS("success"),
	@JsonProperty("failed")
	FAILED("failed"),
	@JsonProperty("didNotRun")
	DID_NOT_RUN("didNotRun");
	
	String message;

	private ExecutionOutcome(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
