package de.unituebingen.validator.rest.representations.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CheckType {
	@JsonProperty("name")
	FILENAME("file name"), @JsonProperty("verapdf")
	VERAPDF_POLICY("verapdf policy"), @JsonProperty("fits")
	FITS_RESULT("fits result");

	String type;

	private CheckType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
