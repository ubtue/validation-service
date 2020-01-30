package de.unituebingen.validator.persistence.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VeraPdfExecutionMode {
	@JsonProperty("always")
	ALWAYS,
	@JsonProperty("extension")
	FILE_EXTENSION,
	@JsonProperty("fits")
	FITS_PDF_MIME,
	@JsonProperty("never")
	NEVER;
}
