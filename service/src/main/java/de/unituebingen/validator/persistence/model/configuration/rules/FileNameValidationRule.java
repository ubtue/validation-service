package de.unituebingen.validator.persistence.model.configuration.rules;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Entity
public class FileNameValidationRule extends Rule{
	
	@NotNull 
	@Enumerated(EnumType.STRING)
	private FileNameValidationType type;
	
	@NotNull
	private String comparisonValue;

	
	// Setters and Getters
	
	public FileNameValidationType getType() {
		return type;
	}

	public void setType(FileNameValidationType type) {
		this.type = type;
	}

	public String getComparisonValue() {
		return comparisonValue;
	}

	public void setComparisonValue(String comparisonValue) {
		this.comparisonValue = comparisonValue;
	}

	@Override
	public String toString() {
		return "FileNameValidationRule [type=" + type + ", comparisonValue=" + comparisonValue + "]";
	}
	
	

}
