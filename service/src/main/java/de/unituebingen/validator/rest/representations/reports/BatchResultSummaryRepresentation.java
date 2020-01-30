package de.unituebingen.validator.rest.representations.reports;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.unituebingen.validator.rest.representations.ValidationOutcome;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BatchResultSummaryRepresentation {

	private long problematicFiles;
	private long totalFiles;
	private ValidationOutcome validationOutcome;

	public long getProblematicFiles() {
		return problematicFiles;
	}

	public void setProblematicFiles(long filesWithFailedCustomChecks) {
		this.problematicFiles = filesWithFailedCustomChecks;
	}

	public long getTotalFiles() {
		return totalFiles;
	}

	public void setTotalFiles(long totalFiles) {
		this.totalFiles = totalFiles;
	}

	public ValidationOutcome getValidationOutcome() {
		return validationOutcome;
	}

	public void setValidationOutcome(ValidationOutcome validationOutcome) {
		this.validationOutcome = validationOutcome;
	}

}
