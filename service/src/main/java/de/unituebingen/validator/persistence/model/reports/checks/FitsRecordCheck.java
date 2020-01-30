package de.unituebingen.validator.persistence.model.reports.checks;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationType;

@Entity
public class FitsRecordCheck extends Check{

	@Enumerated(EnumType.STRING)
	private FitsRecordValidationType validationType;
		
	private String extension;
	private String puid;
	private String mime;
	private String toolName;
	
	
	// Setters and Getters
	
	public FitsRecordValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(FitsRecordValidationType validationType) {
		this.validationType = validationType;
	}
		
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}
	
	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	
}
