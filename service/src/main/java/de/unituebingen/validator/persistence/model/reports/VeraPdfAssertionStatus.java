package de.unituebingen.validator.persistence.model.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VeraPdfAssertionStatus {
	@JsonProperty("passed")
	PASSED,
	@JsonProperty("failed")
	FAILED;
}
