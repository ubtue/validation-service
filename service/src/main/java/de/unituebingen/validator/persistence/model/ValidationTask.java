package de.unituebingen.validator.persistence.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;



@Entity
@NamedQueries ({
	@NamedQuery(name = ValidationTask.COUNT_FILE_REPORTS_FOR_TASK_ID, 
			query = "SELECT size(v.fileValidationReports) FROM ValidationTask v WHERE v.id=?1 AND v.deleted=false"
	),
	@NamedQuery(name = ValidationTask.COUNT_ALL_PROCESSING, 
			query = "SELECT COUNT(t) FROM ValidationTask t WHERE t.deleted=false AND t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.PROCESSING"
	),
	@NamedQuery(name = ValidationTask.COUNT_ALL_PROCESSING_AND_QUEUED, 
			query = "SELECT COUNT(t) from ValidationTask t WHERE t.deleted=false AND (t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.PROCESSING OR "
					+ "t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.QUEUED)"
	),
	@NamedQuery(name = ValidationTask.COUNT_ALL_FINISHED, 
			query = "SELECT Count(t) from ValidationTask t WHERE t.deleted=false AND (t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.FINISHED OR"
					+ " t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.FAILED)"
	),
	@NamedQuery(name = ValidationTask.COUNT_ALL_FINISHED_FOR_BATCH, 
			query = "SELECT Count(t) from ValidationTask t WHERE t.deleted=false AND (t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.FINISHED OR"
					+ " t.validationStatus = de.unituebingen.validator.persistence.model.ValidationStatus.FAILED) AND t.batch = ?1"
	)
})
public class ValidationTask extends Persistable{
	
	//Query constants
	public static final String COUNT_ALL_FINISHED = "ValidationTask.countAllFinished";
	public static final String COUNT_ALL_FINISHED_FOR_BATCH = "ValidationTask.countAllFinishedForBatchId";
	public static final String COUNT_FILE_REPORTS_FOR_TASK_ID = "ValidationTask.countFileReportsForTaskd";
	public static final String COUNT_ALL_PROCESSING_AND_QUEUED = "ValidationTask.countAllProcessingandQueued";
	public static final String COUNT_ALL_PROCESSING = "ValidationTask.countProcessing";
			
	@OneToMany(cascade= {CascadeType.PERSIST}, orphanRemoval=true, mappedBy="task")
	List<FileValidationReport> fileValidationReports = new ArrayList<>();
	
	@ManyToOne
	private Batch batch;
	
	@ManyToOne(cascade= {CascadeType.PERSIST},fetch=FetchType.EAGER)
	ProcessorConfiguration processorConfiguration;
				
	@Enumerated(EnumType.STRING)
	ValidationStatus validationStatus;
			
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingFinished;
	
	@Embedded
	private ErrorMessage errorMessage;
	
	@Embedded
	TaskResultSummary resultSummary;
	
	private boolean deleted;
	
	
	public void preRemove() {
		if(processorConfiguration != null)
			processorConfiguration.removeValidationTask(this);
		if(batch != null) {
			batch.getValidationTasks().remove(this);
			this.batch = null;
		}
			
		for (FileValidationReport fileValidationReport : fileValidationReports) {
			fileValidationReport.preRemove();
		}
		fileValidationReports.clear();	
	}
	
	public void addFileValidationReport(FileValidationReport report) {
		if(!fileValidationReports.contains(report))
			fileValidationReports.add(report);
		
		report.setTask(this);
	}
	
	
	// Generated Getters and Setters
	
	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public ValidationStatus getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(ValidationStatus validationStatus) {
		this.validationStatus = validationStatus;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public TaskResultSummary getResultSummary() {
		return resultSummary;
	}

	public void setResultSummary(TaskResultSummary resultSummary) {
		this.resultSummary = resultSummary;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ProcessorConfiguration getProcessorConfiguration() {
		return processorConfiguration;
	}

	public void internalSetProcessorConfiguration(ProcessorConfiguration processorConfiguration) {
		this.processorConfiguration = processorConfiguration;
	}
	
	public void removeValidationReport(FileValidationReport report) {
		this.fileValidationReports.remove(report);
	}

	public List<FileValidationReport> getFileValidationReports() {
		return fileValidationReports;
	}

	public void setFileValidationReports(List<FileValidationReport> fileValidationReports) {
		this.fileValidationReports = fileValidationReports;
	}

	public Date getProcessingFinished() {
		return processingFinished;
	}

	public void setProcessingFinished(Date processingFinished) {
		this.processingFinished = processingFinished;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	
}
