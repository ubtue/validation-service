package de.unituebingen.validator.persistence.model.configuration;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationProfile;

@Entity
public class VeraPdfConfiguration extends Persistable{
	
	@Enumerated(EnumType.STRING)
	private ValidationProfile validationProfile = ValidationProfile.UNSPECIFIED;
	
	@Enumerated(EnumType.STRING)
	private VeraPdfExecutionMode executionMode;
	
	@OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST}, mappedBy="veraPdfConfiguration")
	ProcessorConfiguration configuration;
	
	@Embedded
	private VeraPdfPolicyOptions pdfPolicies;
	
	private boolean reportPassedRules;
	private int failedChecksThreshold = 100;
	private int failedChecksPerRuleDisplayed = 5;
	private boolean failOnInvalidPdfA;

		
	public ValidationProfile getValidationProfile() {
		return validationProfile;
	}

	public void setValidationProfile(ValidationProfile validationProfile) {
		this.validationProfile = validationProfile;
	}


	public boolean isReportPassedRules() {
		return reportPassedRules;
	}

	public void setReportPassedRules(boolean reportPassedRules) {
		this.reportPassedRules = reportPassedRules;
	}


	public int getFailedChecksThreshold() {
		return failedChecksThreshold;
	}

	public void setFailedChecksThreshold(int failedChecksThreshold) {
		this.failedChecksThreshold = failedChecksThreshold;
	}

	public int getFailedChecksPerRuleDisplayed() {
		return failedChecksPerRuleDisplayed;
	}

	public void setFailedChecksPerRuleDisplayed(int failedChecksPerRuleDisplayed) {
		this.failedChecksPerRuleDisplayed = failedChecksPerRuleDisplayed;
	}

	public VeraPdfExecutionMode getExecutionMode() {
		return executionMode;
	}

	public void setExecutionMode(VeraPdfExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	public boolean isFailOnInvalidPdfA() {
		return failOnInvalidPdfA;
	}

	public void setFailOnInvalidPdfA(boolean failOnInvalidPdfA) {
		this.failOnInvalidPdfA = failOnInvalidPdfA;
	}

	public ProcessorConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ProcessorConfiguration configuration) {
		this.configuration = configuration;
		if(configuration.getVeraPdfConfiguration() != this)
			configuration.setVeraPdfConfiguration(this);
	}

	public VeraPdfPolicyOptions getPdfPolicies() {
		return pdfPolicies;
	}

	public void setPdfPolicies(VeraPdfPolicyOptions pdfPolicies) {
		this.pdfPolicies = pdfPolicies;
	}

	
		
}
