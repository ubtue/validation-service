package de.unituebingen.validator.persistence.model;

import javax.persistence.Embeddable;

@Embeddable
public class TaskResultSummary {
	
	private long filesWithProblems;
	private long filesTotal;
	private ValidationOutcome validationOutcome;

	public long getFilesWithProblems() {
		return filesWithProblems;
	}

	public void setFilesWithProblems(long filesWithProblems) {
		this.filesWithProblems = filesWithProblems;
	}
	
	public void increaseFilesWithFailedChecksCount() {
		filesWithProblems++;
	}
	
	public long getFilesTotal() {
		return filesTotal;
	}

	public void setFilesTotal(long filesTotal) {
		this.filesTotal = filesTotal;
	}

	public ValidationOutcome getValidationOutcome() {
		return validationOutcome;
	}

	public void setValidationOutcome(ValidationOutcome validationOutcome) {
		this.validationOutcome = validationOutcome;
	}

	
	

}
