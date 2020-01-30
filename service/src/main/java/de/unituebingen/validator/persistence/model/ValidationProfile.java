package de.unituebingen.validator.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ValidationProfile {
	@JsonProperty("automatic")
	UNSPECIFIED ("unspecified"),
	@JsonProperty("1a")
	PDF_A_1_A ("1a"),
	@JsonProperty("1b")
	PDF_A_1_B ("1b"),
	@JsonProperty("2a")
	PDF_A_2_A ("2a"),
	@JsonProperty("2b")
	PDF_A_2_B ("2b"),
	@JsonProperty("2u")
	PDF_A_2_U ("2u"),
	@JsonProperty("3a")
	PDF_A_3_A ("3a"),
	@JsonProperty("3b")
	PDF_A_3_B ("3b"),
	@JsonProperty("3u")
	PDF_A_3_U ("3u"),
	@JsonProperty("4")
	PDF_A_4 ("4");

	String code;

	private ValidationProfile(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public static ValidationProfile fromCode(String code) {
		switch (code) {
		case "unspecified":
			return ValidationProfile.UNSPECIFIED;
		case "1a":
			return ValidationProfile.PDF_A_1_A;
		case "1b":
			return ValidationProfile.PDF_A_1_B;
		case "2a":
			return ValidationProfile.PDF_A_2_A;
		case "2b":
			return ValidationProfile.PDF_A_2_B;
		case "2u":
			return ValidationProfile.PDF_A_2_U;
		case "3a":
			return ValidationProfile.PDF_A_3_A;
		case "3b":
			return ValidationProfile.PDF_A_3_B;
		case "3u":
			return ValidationProfile.PDF_A_3_U;
		case "4":
			return ValidationProfile.PDF_A_4;
		default:
			return ValidationProfile.UNSPECIFIED;
		}
	}
	
	public static ValidationProfile fromVeraDescription(String description) {
		
		if (description == null)
			return ValidationProfile.UNSPECIFIED;
		
		description = description.substring(0, description.indexOf(" "));
		
		switch (description) {
		case "0":
			return ValidationProfile.UNSPECIFIED;
		case "PDF/A-1A":
			return ValidationProfile.PDF_A_1_A;
		case "PDF/A-1B":
			return ValidationProfile.PDF_A_1_B;
		case "PDF/A-2A":
			return ValidationProfile.PDF_A_2_A;
		case "PDF/A-2B":
			return ValidationProfile.PDF_A_2_B;
		case "PDF/A-2U":
			return ValidationProfile.PDF_A_2_U;
		case "PDF/A-3A":
			return ValidationProfile.PDF_A_3_A;
		case "PDF/A-3B":
			return ValidationProfile.PDF_A_3_B;
		case "PDF/A-3U":
			return ValidationProfile.PDF_A_3_U;
		case "PDF/A-4":
			return ValidationProfile.PDF_A_4;
		default:
			return ValidationProfile.UNSPECIFIED;
		}
	}
	
}
