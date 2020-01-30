package de.unituebingen.validator.persistence.model.configuration.rules;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;

@Entity
public class FitsRecordValidationRule extends Rule{
		
	@NotNull
	@Enumerated(EnumType.STRING)
	private FitsRecordValidationType type;
	
	private String mime;
	
	private String puid;

	private String extension;
	
	private String toolName;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ValidationOutcome outcomeOnMissingInformation;
	
	
	// Setters and Getters
	

	public FitsRecordValidationType getType() {
		return type;
	}

	public void setType(FitsRecordValidationType type) {
		this.type = type;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public ValidationOutcome getOutcomeOnMissingInformation() {
		return outcomeOnMissingInformation;
	}

	public void setOutcomeOnMissingInformation(ValidationOutcome outcomeOnMissingInformation) {
		this.outcomeOnMissingInformation = outcomeOnMissingInformation;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}
	
	
		
}
