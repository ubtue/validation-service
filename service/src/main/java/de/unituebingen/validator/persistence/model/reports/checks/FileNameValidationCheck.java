package de.unituebingen.validator.persistence.model.reports.checks;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;

@Entity
public class FileNameValidationCheck extends Check{

	@Enumerated(EnumType.STRING)
	private FileNameValidationType validationType;
	
	private String comparisonValue;

	
	public FileNameValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(FileNameValidationType validationType) {
		this.validationType = validationType;
	}

	public String getComparisonValue() {
		return comparisonValue;
	}

	public void setComparisonValue(String comparisonValue) {
		this.comparisonValue = comparisonValue;
	}

	
	
	
}
