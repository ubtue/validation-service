package de.unituebingen.validator.tasks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import de.unituebingen.validator.common.Sequencer;
import de.unituebingen.validator.fits.FitsResultParser;
import de.unituebingen.validator.fits.FitsValidation;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfExecutionMode;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.reports.ExecutionOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import de.unituebingen.validator.persistence.model.reports.checks.FileNameValidationCheck;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;
import de.unituebingen.validator.persistence.model.reports.checks.VeraPdfPolicyCheck;
import de.unituebingen.validator.rulecheck.FileNameRuleChecker;
import de.unituebingen.validator.rulecheck.FitsResultRuleChecker;
import de.unituebingen.validator.verapdf.PdfValidationPolicies;
import de.unituebingen.validator.verapdf.VeraPdfReportHandler;
import de.unituebingen.validator.verapdf.VeraPdfValidation;
import edu.harvard.hul.ois.fits.FitsOutput;

/**
 * Class for validating a file upload according to a specific processing
 * configuration.
 *
 */
public class FileValidationTask implements Callable<FileValidationReport> {

	@Inject
	private Logger logger;

	@Resource(name = "baseDirectory")
	private String baseDirectory;

	@Resource(name = "fitsExecutablePath")
	private String fitsExecutionPath;

	@Resource(name = "verapdfExecutablePath")
	private String verapdfExecutionPath;

	/** The upload to validate */
	private FileUpload fileUpload;
	/** The validation configuration to be used */
	private ProcessorConfiguration configuration;
	/** The list of file name validation ruels */
	private List<FileNameValidationRule> fileNameRules;
	/**
	 * Map of pre compiled regex pattern for use in combination with file name rules
	 */
	Map<String, Pattern> regExPatternMap = new HashMap<>();
	/** The list of rules to apply on the fits result records */
	private List<FitsRecordValidationRule> fitsRules;
	/** The custom pdf policy file to be used when running veraPDF */
	private File customPdfPolicy;

	@Override
	public FileValidationReport call() throws Exception {
		try {
			if (!isFileAccessible()) {
				return createFileNotAccessibleReport();
			}

			FileValidationReport fileValRep = new FileValidationReport();

			// FITS

			if (!configuration.isFitsActive()) {
				fileValRep.setFitsExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
			} else {
				executeFits(fileValRep);
			}

			// VeraPDF

			VeraPdfConfiguration veraConfig = this.configuration.getVeraPdfConfiguration();

			if (veraConfig.getExecutionMode() == VeraPdfExecutionMode.FILE_EXTENSION) {
				if (!fileUpload.getFileName().toLowerCase().endsWith(".pdf")) {
					fileValRep.setVeraPdfExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
				}
			} else if (veraConfig.getExecutionMode() == VeraPdfExecutionMode.FITS_PDF_MIME) {
				if (!FitsResultParser.containsRecordWithMimeType("application/pdf", fileValRep.getFitsRecords())) {
					fileValRep.setVeraPdfExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
				}
			} else if (veraConfig.getExecutionMode() == VeraPdfExecutionMode.NEVER) {
				fileValRep.setVeraPdfExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
			}

			if (fileValRep.getVeraPdfExecutionOutcome() == null) {
				executeVeraPDF(fileValRep);
			}

			// Apply FITS and File name rules

			generateFileNameValidationChecks(fileValRep);
			generateFitsRecordChecks(fileValRep);

			// If tool execution hasn't failed (and thus outcome is 'not valid'), determine
			// validation outcome by analyzing generated checks.

			if (fileValRep.getValidationOutcome() != ValidationOutcome.NOT_VALID) {
				resolveOutcomeWithChecks(fileValRep);
			}

			return fileValRep;
		} catch (InterruptedException e) {
			throw new RuntimeException("Validation task was interrupted", e);
		}
	}

	/**
	 * Determines the validation outcome by searching for failed checks.
	 * 
	 * @param fileValRep
	 *            The report to be evaluated.
	 */
	private void resolveOutcomeWithChecks(FileValidationReport fileValRep) {
		if (fileValRep.hasFailedChecks()) {
			fileValRep.setValidationOutcome(ValidationOutcome.NOT_VALID);
		} else {
			fileValRep.setValidationOutcome(ValidationOutcome.VALID);
		}
	}

	/**
	 * Checks whether the file is accessible for the validation task or not.
	 * 
	 * @return true if accessible, else false.
	 */
	private boolean isFileAccessible() {
		try {
			if (!new File(fileUpload.getFilePath()).exists())
				return false;
			return true;
		} catch (SecurityException e) {
			return false;
		}
	}

	/**
	 * Creates a file validation report for the non accessible file use case.
	 * 
	 * @return
	 */
	private FileValidationReport createFileNotAccessibleReport() {
		FileValidationReport fileValRep = new FileValidationReport();
		fileValRep.setValidationOutcome(ValidationOutcome.NOT_VALID);
		fileValRep.setFitsExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
		fileValRep.setVeraPdfExecutionOutcome(ExecutionOutcome.DID_NOT_RUN);
		fileValRep.getErrorMessages().add("File can not be accessed");
		return fileValRep;
	}

	/**
	 * Execute FITS and apply execution results to file validation report. Upon tool
	 * error the outcome of the file validation report will be set to not valid
	 * automatically.
	 * 
	 * @param fileValRep
	 *            The file validation report to be used.
	 * @throws InterruptedException
	 */
	private void executeFits(FileValidationReport fileValRep) throws InterruptedException {
		String fitsOutputPath = baseDirectory + File.separator + String.valueOf(Sequencer.getSequence()) + "_"
				+ UUID.randomUUID().toString() + ".xml";
		FitsValidation fitsValidation = null;
		Process fitsProcess = null;
		try {
			fitsValidation = new FitsValidation(fileUpload.getFilePath(), fitsOutputPath);
			fitsProcess = fitsValidation.getProcessBuilder(fitsExecutionPath).start();

			synchronized (fitsProcess) {
				fitsProcess.waitFor(configuration.getFitsTimeOut(), TimeUnit.SECONDS);
			}

			if (fitsProcess.isAlive()) {
				fitsProcess.destroyForcibly();
				fileValRep.markFitsExecutionAsFailed("FITS execution timed out");
			} else {
				if (fitsProcess.exitValue() != 0) {
					fileValRep.markFitsExecutionAsFailed(
							"FITS execution failed with exit code: " + fitsProcess.exitValue());
				} else {
					List<FitsRecord> fitsRecords = new ArrayList<>();
					FitsOutput output = new FitsOutput(FileUtils.readFileToString(new File(fitsOutputPath), "UTF-8"));
					fitsRecords = FitsResultParser.fromFitsOutput(output);
					for (FitsRecord record : fitsRecords) {
						fileValRep.addFitsRecord(record);
					}
					fileValRep.setFitsExecutionOutcome(ExecutionOutcome.SUCCESS);
				}
			}
		} catch (IOException | JDOMException | SecurityException e) {
			fileValRep.markFitsExecutionAsFailed("FITS execution exited with exception: " + e.getMessage());
			logger.log(Level.INFO, "FITS execution exited with exception: ", e);
		} finally {
			if (fitsProcess != null && fitsProcess.isAlive())
				fitsProcess.destroyForcibly();
			if (fitsValidation != null)
				fitsValidation.clearOutput();
		}
	}

	/**
	 * Execute veraPDF and apply results to file validation report. Upon tool error
	 * the outcome of the file validation report will be set to not valid
	 * automatically.
	 * 
	 * @param fileValRep
	 *            The file validation report to be used.
	 * @throws InterruptedException
	 */
	private void executeVeraPDF(FileValidationReport fileValRep) throws InterruptedException {
		VeraPdfConfiguration veraConfig = this.configuration.getVeraPdfConfiguration();
		Process verapdfProcess = null;
		VeraPdfValidation verapdfValidation = null;

		String validationFilePath = baseDirectory + File.separator + String.valueOf(Sequencer.getSequence()) + "_"
				+ UUID.randomUUID().toString() + ".xml";
		verapdfValidation = new VeraPdfValidation(fileUpload, validationFilePath);
		verapdfValidation.setPolicyFilePath(customPdfPolicy == null ? null : customPdfPolicy.getAbsolutePath());

		try {
			verapdfProcess = verapdfValidation.getProcessBuilder(veraConfig, verapdfExecutionPath, configuration)
					.start();
			synchronized (verapdfProcess) {
				verapdfProcess.waitFor(configuration.getVeraPdfTimeOut(), TimeUnit.SECONDS);
			}

			if (verapdfProcess.isAlive()) {
				verapdfProcess.destroyForcibly();
				fileValRep.markVeraPdfExecutionAsFailed("VeraPDF execution time out reached");
			} else {
				if (verapdfProcess.exitValue() != 0) {
					fileValRep.markVeraPdfExecutionAsFailed("VeraPDF execution failed");
				} else {
					// Parse verapdf report
					try (FileReader reader = new FileReader(validationFilePath);) {
						XMLReader xmlReader = XMLReaderFactory.createXMLReader();
						InputSource inputSource = new InputSource(reader);
						VeraPdfReportHandler reportHandler = new VeraPdfReportHandler();
						xmlReader.setContentHandler(reportHandler);
						xmlReader.parse(inputSource);

						fileValRep.setVeraPdfRecord(reportHandler.getVeraPdfRecord());
						fileValRep.addFailedChecks(reportHandler.getFailedVeraPdfPolicyChecks());

						// Create PDF/A not valid policy check if required by configuration
						if (veraConfig.isFailOnInvalidPdfA() && !reportHandler.getVeraPdfRecord().getCompliant()) {
							VeraPdfPolicyCheck failedCheck = new VeraPdfPolicyCheck();
							failedCheck.setOutcome(ValidationOutcome.NOT_VALID);
							failedCheck.setOriginalRuleOutcome(ValidationOutcome.NOT_VALID);
							failedCheck.setRuleName("Enforce PDF/A compliance");

							if (reportHandler.getVeraPdfRecord().hasExitedExceptionally()) {
								if (reportHandler.getVeraPdfRecord().getEncrypted()) {
									failedCheck.setPolicyKey(
											PdfValidationPolicies.MESSAGE_KEY_PDFA_VALID_ENCRYPTED_POLICY);
									fileValRep.addFailedCheck(failedCheck);
									fileValRep.getVeraPdfRecord().setFailedPolicyChecks(
											fileValRep.getVeraPdfRecord().getFailedPolicyChecks() + 1l);
								}
							} else {
								failedCheck.setPolicyKey(PdfValidationPolicies.MESSAGE_KEY_PDFA_VALID_POLICY);
								fileValRep.addFailedCheck(failedCheck);
								fileValRep.getVeraPdfRecord().setFailedPolicyChecks(
										fileValRep.getVeraPdfRecord().getFailedPolicyChecks() + 1l);
							}
						}

						if (fileValRep.getVeraPdfRecord().hasExitedExceptionally()) {
							fileValRep.markVeraPdfExecutionAsFailed();
						} else {
							fileValRep.setVeraPdfExecutionOutcome(ExecutionOutcome.SUCCESS);
						}
					}
				}
			}
		} catch (IOException | SAXException | SecurityException e) {
			fileValRep.markVeraPdfExecutionAsFailed("VeraPDF execution failed with exception: " + e.getMessage());
		} finally {
			if (verapdfProcess != null && verapdfProcess.isAlive())
				verapdfProcess.destroyForcibly();
			if (verapdfValidation != null)
				verapdfValidation.clearOutput();
		}
	}

	/**
	 * Apply fits result rules and add resulting checks to the file validation
	 * report.
	 * 
	 * @param fileValRep
	 *            The file validation report.
	 */
	private void generateFitsRecordChecks(FileValidationReport fileValRep) {
		List<FitsRecordCheck> fitsRecordChecks = new ArrayList<>();

		if (fileValRep.getFitsExecutionOutcome() == ExecutionOutcome.SUCCESS) {
			for (FitsRecordValidationRule rule : fitsRules) {
				for (FitsRecord fitsRecord : fileValRep.getFitsRecords()) {
					FitsRecordCheck check = FitsResultRuleChecker.checkUploadWithRecordAndRule(rule, fitsRecord,
							fileUpload);
					if (check.getOutcome() == ValidationOutcome.NOT_VALID) {
						fitsRecordChecks.add(check);
					}
				}
			}
		}

		fileValRep.addFailedChecks(fitsRecordChecks);
	}

	/**
	 * Apply file name rules and add resulting checks to the file validation report.
	 * 
	 * @param fileValRep
	 *            The file validation report.
	 */
	private void generateFileNameValidationChecks(FileValidationReport fileValRep) {
		List<FileNameValidationCheck> fileNameChecks = new ArrayList<>();

		for (FileNameValidationRule rule : fileNameRules) {
			FileNameValidationCheck check = FileNameRuleChecker.checkByValidatingUploadWithRule(rule, fileUpload,
					this.regExPatternMap);
			if (check.getOutcome() == ValidationOutcome.NOT_VALID) {
				fileNameChecks.add(check);
			}
		}

		fileValRep.addFailedChecks(fileNameChecks);
	}

	// Setters and Getters

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public ProcessorConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ProcessorConfiguration configuration) {
		this.configuration = configuration;
	}

	public List<FileNameValidationRule> getFileNameRules() {
		return fileNameRules;
	}

	public void setFileNameRules(List<FileNameValidationRule> fileNameRules) {
		this.fileNameRules = fileNameRules;
	}

	public Map<String, Pattern> getRegExPatternMap() {
		return regExPatternMap;
	}

	public void setRegExPatternMap(Map<String, Pattern> regExPatternMap) {
		this.regExPatternMap = regExPatternMap;
	}

	public List<FitsRecordValidationRule> getFitsRules() {
		return fitsRules;
	}

	public void setFitsRules(List<FitsRecordValidationRule> fitsRules) {
		this.fitsRules = fitsRules;
	}

	public File getCustomPdfPolicy() {
		return customPdfPolicy;
	}

	public void setCustomPdfPolicy(File customPdfPolicy) {
		this.customPdfPolicy = customPdfPolicy;
	}

}
