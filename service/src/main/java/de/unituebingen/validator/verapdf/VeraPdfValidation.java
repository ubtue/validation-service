package de.unituebingen.validator.verapdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfPolicyOptions;

public class VeraPdfValidation {

	private String filePath;
	private String validationFilePath;
	private String policyFilePath;
	private File output;

	public VeraPdfValidation(FileUpload upload, String validationFilePath) {
		super();
		this.filePath = upload.getFilePath();
		this.validationFilePath = validationFilePath;
	}

	public ProcessBuilder getProcessBuilder(VeraPdfConfiguration config, String veraPdfJarPath,
			ProcessorConfiguration procCon) throws IOException {
		
		List<String> commandList = new ArrayList<>();
		commandList.add("java");
		commandList.add("-Xmx" + String.valueOf(procCon.getVeraPdfMaxHeapSize()) + "m");
		commandList.add("-jar");
		commandList.add(veraPdfJarPath);
		commandList.add(filePath);
		commandList.add(validationFilePath);
		commandList.add(String.valueOf(config.getFailedChecksThreshold()));
		commandList.add(String.valueOf(config.getFailedChecksPerRuleDisplayed()));
		commandList.add(config.getValidationProfile().getCode());

		if (policyFilePath != null)
			commandList.add(policyFilePath);

		output = new File(validationFilePath + "_output");
		FileUtils.touch(output);

		ProcessBuilder processBuilder = new ProcessBuilder(commandList).redirectOutput(this.output)
				.redirectErrorStream(true);

		return processBuilder;
	}

	public String getPolicyFilePath() {
		return policyFilePath;
	}

	public void setPolicyFilePath(String policyFilePath) {
		this.policyFilePath = policyFilePath;
	}

	public void clearOutput() {
		FileUtils.deleteQuietly(this.output);
		FileUtils.deleteQuietly(new File(this.validationFilePath));
	}

	public static final File createPdfPolicyFile(String filePath, VeraPdfPolicyOptions options) throws IOException {

		List<String> customPolicies = new ArrayList<>();

		if (options.isDisallowEmbeddedFiles()) {
			customPolicies.add(PdfValidationPolicies.RULE_EMBEDDED_FILES);
		}
		if (options.isDisallowEmbeddedFonts()) {
			customPolicies.add(PdfValidationPolicies.RULE_EMBEDDED_FONTS);
		}
		if (options.isDisallowEncryptInTrailer()) {
			customPolicies.add(PdfValidationPolicies.RULE_TRAILER_ENCRYPT);
		}
		if (options.isDisallowFileAttachments()) {
			customPolicies.add(PdfValidationPolicies.RULE_ATTACHMENTS);
		}
		if (options.isDisallowMultimediaAnnotations()) {
			customPolicies.add(PdfValidationPolicies.RULE_MULTIMEDIA);
		}
		if (options.isDisallowNonParseableDocuments()) {
			customPolicies.add(PdfValidationPolicies.RULE_DOCUMENT_PARSABLE);
		}
		if (options.isDisallowEncryption()) {
			customPolicies.add(PdfValidationPolicies.RULE_ENCRYPT);
		}

		if (customPolicies.size() == 0)
			return null;

		String policyComplete = "<?xml version=\"1.0\"?>"
				+ "<sch:schema xmlns:sch=\"http://purl.oclc.org/dsdl/schematron\" queryBinding=\"xslt\">";

		for (String policy : customPolicies) {
			policyComplete += policy;
		}
		policyComplete += "</sch:schema>";

		FileUtils.writeStringToFile(new File(filePath), policyComplete, "UTF-8");

		return new File(filePath);
	}

}
