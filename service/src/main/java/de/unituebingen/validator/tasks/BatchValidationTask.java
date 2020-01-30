package de.unituebingen.validator.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import de.unituebingen.validator.beans.ReportAssistantBean;
import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.common.Sequencer;
import de.unituebingen.validator.common.exceptions.ReportAssistantException;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ErrorMessage;
import de.unituebingen.validator.persistence.model.ErrorType;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.TaskResultSummary;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.reports.ExecutionOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.verapdf.VeraPdfValidation;

public class BatchValidationTask implements Runnable {

	@Resource(name = "baseDirectory")
	private String baseDirectory;

	@Resource(name = "fitsExecutablePath")
	private String fitsExecutionPath;

	@Resource(name = "verapdfExecutablePath")
	private String verapdfExecutionPath;

	@Inject
	private Logger logger;

	@Inject
	ReportAssistantBean reportAssistant;

	@Inject
	SetupBean setup;

	@Resource
	ManagedExecutorService executorService;

	@Inject
	@Task
	Event<Long> taskFinishedEvent;

	@Inject
	Instance<FileValidationTask> fileTaskInstance;
	private Long taskId;
	private HashMap<Future<FileValidationReport>, FileUpload> futureUploadMap = new HashMap<>();
	private boolean taskProcessingFailed;
	private AtomicLong failedReportsCounter = new AtomicLong(0l);
	private AtomicLong reportsCounter = new AtomicLong(0l);

	@Override
	public void run() {
		ValidationTask task = reportAssistant.getValidationTask(taskId);
		List<Future<FileValidationReport>> futureList = new ArrayList<>();
		File customPdfPolicy = null;
		try {
			if (task == null)
				return;

			Batch batch = task.getBatch();
			if (batch == null) {
				reportAssistant.updateValidationTask(task.getId(), ValidationStatus.FAILED,
						new ErrorMessage("Batch gone", ErrorType.DELETED_ENTITY.getDescription()));
				return;
			}

			ProcessorConfiguration configuration = task.getProcessorConfiguration();
			if (configuration == null) {
				reportAssistant.updateValidationTask(task.getId(), ValidationStatus.FAILED,
						new ErrorMessage("Validation configuration gone", ErrorType.DELETED_ENTITY.getDescription()));
				return;
			}

			// Fetch upload page for first loop
			long lastId = 0;
			int retrieveLimit = (int) setup.getGlobalSettings().getConcurrenThreadsPerTask();
			List<FileUpload> uploadsPage = reportAssistant.getFileUploadPage(batch.getId(), retrieveLimit, lastId);

			// Fetch rules
			List<FileNameValidationRule> fileNameRules = reportAssistant
					.getFileNameValidationRules(task.getProcessorConfiguration());
			List<FitsRecordValidationRule> fitsRules = reportAssistant
					.getFitsRecordValidationRules(task.getProcessorConfiguration());

			// Create custom pdf policy file
			VeraPdfConfiguration veraConfig = configuration.getVeraPdfConfiguration();
			String policyFilePath = baseDirectory + File.separator + String.valueOf(Sequencer.getSequence()) + "_"
					+ UUID.randomUUID().toString() + ".sch";
			customPdfPolicy = VeraPdfValidation.createPdfPolicyFile(policyFilePath, veraConfig.getPdfPolicies());

			Map<String, Pattern> patternMap = new HashMap<>();

			// Pre-compile regex patterns
			for (FileNameValidationRule rule : fileNameRules) {
				if (rule.getType() == FileNameValidationType.REGEX_MATCHES
						|| rule.getType() == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
					if (!patternMap.containsKey(rule.getComparisonValue())) {
						try {
							Pattern pattern = Pattern.compile(rule.getComparisonValue());
							patternMap.put(rule.getComparisonValue(), pattern);
						} catch (Exception e) {
							throw new ReportAssistantException(
									"Failed to compile regex pattern: " + rule.getComparisonValue());
						}
					}
				}
			}

			// Process files
			do {
				if (uploadsPage.isEmpty())
					break;

				for (FileUpload fileUpload : uploadsPage) {
					FileValidationTask fileValTask = fileTaskInstance.get();
					fileValTask.setFileUpload(fileUpload);
					fileValTask.setConfiguration(configuration);
					fileValTask.setCustomPdfPolicy(customPdfPolicy);
					fileValTask.setFileNameRules(fileNameRules);
					fileValTask.setFitsRules(fitsRules);
					fileValTask.setRegExPatternMap(patternMap);

					Future<FileValidationReport> future = executorService.submit(fileValTask);
					futureList.add(future);
					futureUploadMap.put(future, fileUpload);
				}

				while (countDoneFileValidationFutures(futureList) == 0) {
					Thread.sleep(1000);
				}

				// query next page
				lastId = uploadsPage.get(uploadsPage.size() - 1).getId();
				uploadsPage = reportAssistant.getFileUploadPage(batch.getId(),
						countDoneFileValidationFutures(futureList), lastId);
				handleDoneFileValidationFutures(futureList);

			} while (uploadsPage.size() != 0);

			for (Future<FileValidationReport> future : futureList) {
				handleFuture(future);
			}

			// Create Summary
			TaskResultSummary summary = new TaskResultSummary();
			summary.setFilesWithProblems(failedReportsCounter.get());
			summary.setFilesTotal(reportsCounter.get());

			if (failedReportsCounter.get() == 0l && !taskProcessingFailed) {
				summary.setValidationOutcome(ValidationOutcome.VALID);
			} else {
				summary.setValidationOutcome(ValidationOutcome.NOT_VALID);
			}

			reportAssistant.finishValidationTask(task.getId(),
					taskProcessingFailed == false ? ValidationStatus.FINISHED : ValidationStatus.FAILED, summary);

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occured while processing validation task with id: " + taskId, e);

			for (Future<FileValidationReport> future : futureList) {
				future.cancel(true);
			}

			try {
				reportAssistant.updateValidationTask(task.getId(), ValidationStatus.FAILED,
						new ErrorMessage(e.getMessage(), ErrorType.SERVER_ERROR.getDescription()));
			} catch (ReportAssistantException | EJBException e1) {
				logger.log(Level.SEVERE, "Could not finish validation task processing. Task id: " + taskId, e1);
			}
		} finally {
			taskFinishedEvent.fire(taskId);
			if (customPdfPolicy != null) {
				FileUtils.deleteQuietly(customPdfPolicy);
			}
		}
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	private int countDoneFileValidationFutures(List<Future<FileValidationReport>> futureList) {
		int doneCount = 0;
		for (Future<FileValidationReport> completableFuture : futureList) {
			if (completableFuture.isDone())
				doneCount++;
		}

		return doneCount;
	}

	private void handleDoneFileValidationFutures(List<Future<FileValidationReport>> futureList)
			throws ReportAssistantException {
		for (Iterator<Future<FileValidationReport>> iterator = futureList.iterator(); iterator.hasNext();) {
			Future<FileValidationReport> future = (Future<FileValidationReport>) iterator.next();
			if (future.isDone()) {
				try {
					handleFuture(future);
				} finally {
					iterator.remove();
				}
			}
		}
	}

	private void handleFuture(Future<FileValidationReport> future) throws ReportAssistantException {
		long uploadId = 0;
		try {
			FileValidationReport report = future.get();
			FileUpload upload = futureUploadMap.get(future);
			if (upload == null) {
				throw new ReportAssistantException("Could not recover upload for report future");
			}
			uploadId = upload.getId();
			reportAssistant.assignFileValidationReport(report, upload.getId(), taskId);

			if (report.getValidationOutcome() == ValidationOutcome.NOT_VALID)
				failedReportsCounter.incrementAndGet();

		} catch (CancellationException | InterruptedException | ExecutionException e) {
			handleFailedFuture(future, e, uploadId);
			failedReportsCounter.incrementAndGet();
		} finally {
			futureUploadMap.remove(future);
			reportsCounter.getAndIncrement();
		}
	}

	private void handleFailedFuture(Future<FileValidationReport> future, Exception e, long uploadId)
			throws ReportAssistantException {
		taskProcessingFailed = true;
		FileUpload upload = futureUploadMap.get(future);
		if (upload == null)
			throw new ReportAssistantException("Could not recover upload for failed report future");
		createFailedFileValReport(e, upload.getId(), taskId);
	}

	private void createFailedFileValReport(Exception exception, long uploadId, long taskId)
			throws ReportAssistantException {
		FileValidationReport report = new FileValidationReport();
		report.getErrorMessages().add("Validation task execution failed with exception: " + exception.getMessage());
		report.setFitsExecutionOutcome(ExecutionOutcome.FAILED);
		report.setVeraPdfExecutionOutcome(ExecutionOutcome.FAILED);
		report.setValidationOutcome(ValidationOutcome.NOT_VALID);
		reportAssistant.assignFileValidationReport(report, uploadId, this.taskId);
	}

}
