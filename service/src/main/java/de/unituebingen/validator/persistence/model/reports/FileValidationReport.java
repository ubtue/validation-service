package de.unituebingen.validator.persistence.model.reports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.reports.checks.Check;
import de.unituebingen.validator.persistence.model.reports.checks.FileNameValidationCheck;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;

/**
 * Result report of a file validation
 * @author Fabian Hamm
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = FileValidationReport.GET_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES, 
		query = "SELECT f FROM FileValidationReport f WHERE f.task.deleted=false AND f.task.id=?1 AND UPPER(f.fileUpload.fileName) LIKE UPPER(?2) AND f.validationOutcome IN ?3 "
	),
	@NamedQuery(name = FileValidationReport.GET_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES, 
		query = "SELECT f FROM FileValidationReport f WHERE f.task.deleted=false AND f.task.id=?1 AND f.validationOutcome IN ?2"
	),
	@NamedQuery(name = FileValidationReport.COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES, 
		query = "SELECT COUNT (f) FROM FileValidationReport f WHERE f.task.deleted=false AND f.task.id=?1 AND UPPER(f.fileUpload.fileName) LIKE UPPER(?2) AND f.validationOutcome IN ?3"
	),
	@NamedQuery(name = FileValidationReport.COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES, 
		query = "SELECT COUNT (f) FROM FileValidationReport f WHERE f.task.deleted=false AND f.task.id=?1 AND f.validationOutcome IN ?2"
	)
})
public class FileValidationReport extends Persistable {

	// Query constants
	public static final String COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES = "ValidationTask.countForTaskFilterNameAndValidationOutcome";
	public static final String GET_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES = "ValidationTask.getFileReportsForTaskIdFilterByFileNameAndOutcome";
	public static final String GET_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES = "ValidationTask.getFileReportsForTaskIdFilterByOutcome";
	public static final String COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES = "ValidationTask.countForTaskFilterValidationOutcome";
	
	@OneToMany(cascade= {CascadeType.PERSIST}, orphanRemoval=true, fetch=FetchType.EAGER, mappedBy="report")
	private List<FitsRecord> fitsRecords = new ArrayList<>();
	
	@OneToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true, fetch=FetchType.EAGER, mappedBy="report")
	private VeraPdfRecord veraPdfRecord;
		
	@OneToMany(cascade= {CascadeType.PERSIST}, orphanRemoval=true, mappedBy="report")
	private List<Check> failedChecks = new ArrayList<>();
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	FileUpload fileUpload;
	
	@Enumerated(EnumType.STRING)
	private ValidationOutcome validationOutcome;
	
	@Enumerated(EnumType.STRING)
	private ExecutionOutcome fitsExecutionOutcome;
	
	@Enumerated(EnumType.STRING)
	private ExecutionOutcome veraPdfExecutionOutcome;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	ValidationTask task;
	
	@ElementCollection
	@Column(length=1200)
	private List<String> errorMessages = new ArrayList<>();
	
	private int failedNameChecks;
	private int failedFitsChecks;
	
	public boolean isDeleted() {
		if (task != null) {
			return task.isDeleted();
		}
		return false;
	}
	
	public boolean hasFailedChecks() {
		if((failedChecks != null) && (failedChecks.size() != 0))
			return true;
		
		return false;
	}
	
	/**
	 * Marks the veraPDF execution as failed.
	 * @param errorMessage The reason why it failed
	 */
	public void markVeraPdfExecutionAsFailed(String errorMessage) {
		this.getErrorMessages().add(errorMessage);
		this.setVeraPdfExecutionOutcome(ExecutionOutcome.FAILED);
	   	this.setValidationOutcome(ValidationOutcome.NOT_VALID);
	}
	
	/**
	 * Marks the veraPDF execution as failed without giving a specific reason
	 */
	public void markVeraPdfExecutionAsFailed() {
		this.setVeraPdfExecutionOutcome(ExecutionOutcome.FAILED);
	   	this.setValidationOutcome(ValidationOutcome.NOT_VALID);
	}
	
	/**
	 * Marks the fits execution as failed.
	 * @param errorMessage The reason why it failed
	 */
	public void markFitsExecutionAsFailed(String errorMessage) {
		this.getErrorMessages().add(errorMessage);
		this.setFitsExecutionOutcome(ExecutionOutcome.FAILED);									
		this.setValidationOutcome(ValidationOutcome.NOT_VALID);
	}
	
	@PrePersist
	public void calculateData() {
		this.failedNameChecks = 0;
		this.failedFitsChecks = 0;
		for(Check check : this.getFailedChecks()) {
			if (check instanceof FileNameValidationCheck) {
				failedNameChecks++;
			} else if(check instanceof FitsRecordCheck){
				failedFitsChecks++;
			} 
		}
	}
	
	
	public void preRemove() {
		if(this.fileUpload != null)
			this.fileUpload.getValidationReports().remove(this);
		this.fileUpload = null;
		this.task = null;
		
		for (Iterator iterator = failedChecks.iterator(); iterator.hasNext();) {
			Check check = (Check) iterator.next();
			check.setReport(null);
			iterator.remove();
		}
		
		for (Iterator iterator = fitsRecords.iterator(); iterator.hasNext();) {
			FitsRecord record = (FitsRecord) iterator.next();
			record.setReport(null);
			iterator.remove();
		}
		
		if(this.veraPdfRecord != null) {
			this.veraPdfRecord.setReport(null);
			this.veraPdfRecord = null;
		}

	}
	

	public List<FitsRecord> getFitsRecords() {
		return fitsRecords;
	}
	
	public void addFitsRecord(FitsRecord record) {
		if(!this.fitsRecords.contains(record)) {
			this.fitsRecords.add(record);
		}
		record.setReport(this);
	}

	public VeraPdfRecord getVeraPdfRecord() {
		return veraPdfRecord;
	}

	public void setVeraPdfRecord(VeraPdfRecord veraPdfRecord) {
		this.veraPdfRecord = veraPdfRecord;
		veraPdfRecord.setReport(this);
	}


	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public ValidationOutcome getValidationOutcome() {
		return validationOutcome;
	}

	public void setValidationOutcome(ValidationOutcome validationOutcome) {
		this.validationOutcome = validationOutcome;
	}

	public List< Check> getFailedChecks() {
		return failedChecks;
	}

	public void setFailedChecks(List<Check> failedChecks) {
		this.failedChecks = failedChecks;
		for (Check check : failedChecks) {
			check.setReport(this);
		}
	}
	
	public void addFailedChecks(List<? extends Check> failedChecks) {
		for (Check check : failedChecks) {
			check.setReport(this);
		}
		this.failedChecks.addAll(failedChecks);
	}
	
	public void addFailedCheck(Check check) {
		
		check.setReport(this);
		
		this.failedChecks.add(check);
	}

	public ValidationTask getTask() {
		return task;
	}

	public void setTask(ValidationTask task) {
		this.task = task;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public ExecutionOutcome getFitsExecutionOutcome() {
		return fitsExecutionOutcome;
	}

	public void setFitsExecutionOutcome(ExecutionOutcome fitsExecutionOutcome) {
		this.fitsExecutionOutcome = fitsExecutionOutcome;
	}

	public ExecutionOutcome getVeraPdfExecutionOutcome() {
		return veraPdfExecutionOutcome;
	}

	public void setVeraPdfExecutionOutcome(ExecutionOutcome veraPdfExecutionOutcome) {
		this.veraPdfExecutionOutcome = veraPdfExecutionOutcome;
	}

	public int getFailedNameChecks() {
		return failedNameChecks;
	}

	public void setFailedNameChecks(int failedNameChecks) {
		this.failedNameChecks = failedNameChecks;
	}

	public int getFailedFitsChecks() {
		return failedFitsChecks;
	}

	public void setFailedFitsChecks(int failedFitsChecks) {
		this.failedFitsChecks = failedFitsChecks;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

		
	
}
