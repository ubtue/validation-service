package de.unituebingen.validator.persistence.model.configuration.rules;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FitsRecordValidationType {
	
	@JsonProperty("mimeExtension")
	MIME_TYPE_HAS_FILE_EXTENSION("mime type has file extension"),
	@JsonProperty("mimeValid")
	MIME_IS_VALID("mime type is valid"),
	@JsonProperty("puidValid")
	PUID_IS_VALID("file with puid is valid"),
	@JsonProperty("fileValid")
	FILE_IS_VALID("file is valid");
	
	private String value;

	private FitsRecordValidationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
