package de.unituebingen.validator.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ValidationStatus {
	@JsonProperty("queued")
	QUEUED,
	@JsonProperty("processing")
	PROCESSING,
	@JsonProperty("finished")
	FINISHED,
	@JsonProperty("failed")
	FAILED;
}
