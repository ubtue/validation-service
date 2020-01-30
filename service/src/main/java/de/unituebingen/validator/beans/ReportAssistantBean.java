package de.unituebingen.validator.beans;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import de.unituebingen.validator.common.exceptions.ReportAssistantException;
import de.unituebingen.validator.common.qualifiers.Files;
import de.unituebingen.validator.common.qualifiers.Remove;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ErrorMessage;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.TaskResultSummary;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.reports.ExecutionOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.persistence.repository.BatchRepository;
import de.unituebingen.validator.persistence.repository.FileUploadRepository;
import de.unituebingen.validator.persistence.repository.FileValidationReportRepository;
import de.unituebingen.validator.persistence.repository.RuleRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;

@Stateless
/**
 * Bean with assistant methods for processing of validation tasks.
 *
 */
public class ReportAssistantBean {

	@Inject
	ValidationTaskRepository taskRepository;
	@Inject
	FileUploadRepository fileRepository;
	@Inject
	BatchRepository batchRepository;
	@Inject
	FileValidationReportRepository reportRepository;
	@Inject
	RuleRepository ruleRepository;
	@Inject
	@Remove
	@Files
	Event<String> removePathEvent;
	@Inject
	Logger logger;

	public ValidationTask getValidationTask(long id) {
		return taskRepository.findBy(id);
	}

	public List<ValidationTask> getQueuedValidationTasksByDateAsc() {
		return taskRepository.findQueuedOrdeByCreationAsc();
	}

	public Long getProcessingValidationTasksCount() {
		return taskRepository.countProcessing();
	}

	/**
	 * Retrieve a page set of file uploads ordered by id ascending, starting after a
	 * specified id.
	 * 
	 * @param batchId
	 *            The id of the batch.
	 * @param maxResults
	 *            The number of results to be retrieved.
	 * @param lastId
	 *            The id after which the retrieved page shall start.
	 * @return
	 * @throws ReportAssistantException
	 */
	public List<FileUpload> getFileUploadPage(Long batchId, int maxResults, long lastId)
			throws ReportAssistantException {
		try {
			Batch batch = batchRepository.findBy(batchId);
			return fileRepository.findByBatchStartingAfterIdAsc(batch, lastId, maxResults);
		} catch (Exception e) {
			throw new ReportAssistantException(e);
		}
	}

	/**
	 * Updates the state of a ValidationTask
	 * 
	 * @param taskId
	 *            The id of the task to be updated.
	 * @param status
	 *            The new status.
	 * @param message
	 *            The error message, if any.
	 * @return The updated task.
	 * @throws ReportAssistantException
	 */
	public ValidationTask updateValidationTask(Long taskId, ValidationStatus status, ErrorMessage message)
			throws ReportAssistantException {
		try {
			ValidationTask task = taskRepository.findBy(taskId);
			task.setValidationStatus(status);
			if (status == ValidationStatus.FAILED || status == ValidationStatus.FINISHED)
				task.setProcessingFinished(new Date());
			task.setErrorMessage(message);
			return task;
		} catch (Exception e) {
			throw new ReportAssistantException(e);
		}
	}

	/**
	 * Finalizes the specified validation task.
	 * 
	 * @param taskId
	 *            The task to be updated.
	 * @param status
	 *            The status to be set.
	 * @param summary
	 *            The result summary to be assigned.
	 * @return the updated task.
	 * @throws ReportAssistantException
	 */
	public ValidationTask finishValidationTask(Long taskId, ValidationStatus status, TaskResultSummary summary)
			throws ReportAssistantException {
		try {
			ValidationTask task = taskRepository.findBy(taskId);
			task.setValidationStatus(status);
			task.setResultSummary(summary);
			if (status == ValidationStatus.FAILED || status == ValidationStatus.FINISHED)
				task.setProcessingFinished(new Date());
			return task;
		} catch (Exception e) {
			throw new ReportAssistantException(e);
		}
	}

	/**
	 * Assigns a FileValidationReport to a ValidationTask.
	 * 
	 * @param report
	 *            The report to be assigned.
	 * @param uploadId
	 *            The id of the corresponding FileUpload.
	 * @param taskId
	 *            The id of the ValidationTask.
	 * @return The FileValidationReport.
	 * @throws ReportAssistantException
	 */
	public FileValidationReport assignFileValidationReport(FileValidationReport report, long uploadId, long taskId)
			throws ReportAssistantException {
		FileUpload fileUpload = fileRepository.findBy(uploadId);
		if (fileUpload == null)
			throw new ReportAssistantException("file upload not found");
		reportRepository.persist(report);
		fileUpload.addValidationReport(report);
		ValidationTask task = taskRepository.findBy(taskId);
		task.addFileValidationReport(report);
		return report;
	}

	/**
	 * Creates a failed FileValidationReport for a given exception.
	 * 
	 * @param exception
	 *            The exception.
	 * @param uploadId
	 *            The id of the upload.
	 * @param taskId
	 *            The id of the task.
	 * @return The created FileValidationReport.
	 * @throws ReportAssistantException
	 */
	public FileValidationReport createFailedFileValidationReport(Exception exception, long uploadId, long taskId)
			throws ReportAssistantException {
		FileUpload fileUpload = fileRepository.findBy(uploadId);
		if (fileUpload == null)
			throw new ReportAssistantException("File upload not found");
		FileValidationReport report = new FileValidationReport();
		report.getErrorMessages().add("Validation task failed with exception: " + exception.getMessage());
		report.setFitsExecutionOutcome(ExecutionOutcome.FAILED);
		report.setVeraPdfExecutionOutcome(ExecutionOutcome.FAILED);
		report.setValidationOutcome(ValidationOutcome.NOT_VALID);
		reportRepository.persist(report);
		fileUpload.addValidationReport(report);
		ValidationTask task = taskRepository.findBy(taskId);
		task.addFileValidationReport(report);
		return report;
	}

	public List<FitsRecordValidationRule> getFitsRecordValidationRules(ProcessorConfiguration procCon) {
		List<FitsRecordValidationRule> fitsRules = ruleRepository
				.findByTypeForConfigurationId(FitsRecordValidationRule.class, procCon.getId());
		return fitsRules;
	}

	public List<FileNameValidationRule> getFileNameValidationRules(ProcessorConfiguration procCon) {
		List<FileNameValidationRule> fileNameRules = ruleRepository
				.findByTypeForConfigurationId(FileNameValidationRule.class, procCon.getId());
		return fileNameRules;
	}

}
