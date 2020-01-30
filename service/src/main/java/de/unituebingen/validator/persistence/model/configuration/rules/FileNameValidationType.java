package de.unituebingen.validator.persistence.model.configuration.rules;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FileNameValidationType {
	@JsonProperty("contains")
	CONTAINS("contains"),
	@JsonProperty("startsWith")
	STARTS_WTIH("starts with"),
	@JsonProperty("endsWith")
	ENDS_WTIH("ends with"),
	@JsonProperty("equals")
	EQUALS("equals"),
	@JsonProperty("matchesRegularExpression")
	REGEX_MATCHES("regex matches"),
	
	@JsonProperty("doesNotContain")
	DOES_NOT_CONTAIN("does not contain"),
	@JsonProperty("doesNotStartWith")
	DOES_NOT_START_WTIH("does not start with"),
	@JsonProperty("doesNotEndWith")
	DOES_NOT_END_WTIH("does not end with"),
	@JsonProperty("doesNotEqual")
	DOES_NOT_EQUAL("does not equal"),
	@JsonProperty("doesNotMatchRegularExpression")
	REGEX_DOES_NOT_MATCH("regex does not match");
	
	
	private String value;

	private FileNameValidationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


}
