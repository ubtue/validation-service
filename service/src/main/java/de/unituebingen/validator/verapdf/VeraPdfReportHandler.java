package de.unituebingen.validator.verapdf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.ValidationProfile;
import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertion;
import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertionStatus;
import de.unituebingen.validator.persistence.model.reports.VeraPdfRecord;
import de.unituebingen.validator.persistence.model.reports.checks.VeraPdfPolicyCheck;

public class VeraPdfReportHandler extends DefaultHandler {

	public static final String ENCRYPTION_EXCEPTION_KEY = "The PDF stream appears to be encrypted";

	private boolean buildInformation;
	private boolean jobs;
	private boolean batchSummary;
	private boolean item;
	private boolean name;
	private boolean validationReport;
	private boolean rule;
	private boolean description;
	private boolean object;
	private boolean test;
	private boolean failedCheck;
	private boolean context;
	private boolean job;
	private boolean taskResult;
	private boolean exceptionMessage;
	private boolean policyReport;
	private boolean failedChecks;

	private VeraPdfRecord veraRecord;
	private VeraPdfAssertion tempAssertion;
	private VeraPdfPolicyCheck tempCheck;
	private List<VeraPdfPolicyCheck> failedVeraPdfPolicyChecks = new ArrayList<>();

	StringBuffer buffer = new StringBuffer();

	/**
	 * Flushes the character buffer upon element opening / closing
	 */
	private void flushCharacters() {
		if (buffer.length() == 0)
			return;
		buffer.setLength(0);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		buffer.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		super.startElement(uri, localName, qName, attributes);
		flushCharacters();

		if (localName.equals("buildInformation")) {
			buildInformation = true;
		}

		if (localName.equals("jobs")) {
			jobs = true;
		}

		if (localName.equals("job")) {
			job = true;
			veraRecord = new VeraPdfRecord();
			veraRecord.setExitedExceptionally(false);
			veraRecord.setCompliant(false);
			veraRecord.setEncrypted(false);
			veraRecord.setFailedPolicyChecks(0l);
		}

		if (localName.equals("item")) {
			item = true;
		}

		if (localName.equals("name")) {
			name = true;
		}

		if (localName.equals("validationReport")) {
			validationReport = true;
			veraRecord.setValidationProfile(ValidationProfile.fromVeraDescription(attributes.getValue("profileName")));
			veraRecord.setCompliant(Boolean.valueOf(attributes.getValue("isCompliant")) == true ? true : false);
		}

		if (localName.equals("details") && validationReport) {
			veraRecord.setPassedChecks(Long.valueOf(attributes.getValue("passedChecks")));
			veraRecord.setFailedChecks(Long.valueOf(attributes.getValue("failedChecks")));
			veraRecord.setPassedRules(Long.valueOf(attributes.getValue("passedRules")));
			veraRecord.setFailedRules(Long.valueOf(attributes.getValue("failedRules")));
		}

		if (localName.equals("taskResult")) {
			taskResult = true;
		}

		if (localName.equals("exceptionMessage")) {
			exceptionMessage = true;
		}

		if (localName.equals("rule") && validationReport) {
			tempAssertion = new VeraPdfAssertion();
			tempAssertion.setClause(attributes.getValue("clause"));
			tempAssertion.setSpecification(attributes.getValue("specification"));
			tempAssertion.setTestNumber(Integer.valueOf(Integer.valueOf(attributes.getValue("testNumber"))));
			tempAssertion.setStatus(attributes.getValue("status").equals("passed") ? VeraPdfAssertionStatus.PASSED
					: VeraPdfAssertionStatus.FAILED);
			tempAssertion.setPassedChecks(Long.valueOf(attributes.getValue("passedChecks")));
			tempAssertion.setFailedChecks(Long.valueOf(attributes.getValue("failedChecks")));
			veraRecord.getAssertions().add(tempAssertion);
		}

		if (localName.equals("description")) {
			description = true;
		}

		if (localName.equals("object")) {
			object = true;
		}

		if (localName.equals("test")) {
			test = true;
		}

		if (localName.equals("check") && failedChecks) {
			failedCheck = true;
			tempCheck = new VeraPdfPolicyCheck();
			tempCheck.setOutcome(ValidationOutcome.NOT_VALID);
			tempCheck.setTest(attributes.getValue("test"));
			tempCheck.setLocation(attributes.getValue("location"));
		}

		if (localName.equals("context")) {
			context = true;
		}

		if (localName.equals("duration") && job) {
			veraRecord.setStart(new Date(Long.valueOf(attributes.getValue("start"))));
			veraRecord.setFinish(new Date(Long.valueOf(attributes.getValue("finish"))));
		}

		if (localName.equals("batchSummary")) {
			batchSummary = true;
		}

		if (localName.equals("policyReport")) {
			policyReport = true;
			// veraRecord.setFailedPolicyChecks(Long.valueOf(attributes.getValue("failedChecks")));
		}

		if (localName.equals("failedChecks")) {
			failedChecks = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (localName.equals("buildInformation")) {
			buildInformation = false;
		}

		if (localName.equals("jobs")) {
			jobs = false;
		}

		if (localName.equals("job")) {
			job = false;
			veraRecord.setFailedPolicyChecks((long) getFailedVeraPdfPolicyChecks().size());
		}

		if (localName.equals("item")) {
			item = false;
		}

		if (localName.equals("name")) {
			name = false;
		}

		if (localName.equals("taskResult")) {
			taskResult = false;
		}

		if (localName.equals("exceptionMessage")) {
			exceptionMessage = false;
			if (taskResult) {
				veraRecord.setErrorMessage(buffer.toString().trim());
				veraRecord.setExitedExceptionally(true);
				if (buffer.toString().contains(ENCRYPTION_EXCEPTION_KEY))
					veraRecord.setEncrypted(true);
			}
		}

		if (localName.equals("validationReport")) {
			validationReport = false;
		}

		if (localName.equals("description")) {
			description = false;
			tempAssertion.setDescription(buffer.toString().trim());
		}

		if (localName.equals("object")) {
			object = false;
			tempAssertion.setObject(buffer.toString().trim());
		}

		if (localName.equals("test")) {
			test = false;
			tempAssertion.setTest(buffer.toString().trim());
		}

		if (localName.equals("check") && failedChecks) {
			failedCheck = false;
			addFailedPolicyCheck(tempCheck);
		}

		if (localName.equals("context")) {
			context = false;
			tempAssertion.getContexts().add(buffer.toString().trim());
		}

		if (localName.equals("batchSummary")) {
			batchSummary = false;
		}

		if (localName.equals("policyReport")) {
			policyReport = false;
		}

		if (localName.equals("failedChecks")) {
			failedChecks = false;
		}

		if (localName.equals("message") && failedCheck) {
			String message = buffer.toString().trim();
			if (message != null && message.startsWith(PdfValidationPolicies.MESSAGE_KEY_FONTS_POLICY)) {
				tempCheck.setDetails(message.substring(PdfValidationPolicies.MESSAGE_KEY_FONTS_POLICY.length()));
				tempCheck.setPolicyKey(PdfValidationPolicies.MESSAGE_KEY_FONTS_POLICY);
			} else {
				if (!(message == null))
					tempCheck.setPolicyKey(message);
			}
		}

		flushCharacters();
	}

	public VeraPdfRecord getVeraPdfRecord() {
		return veraRecord;
	}

	public List<VeraPdfPolicyCheck> getFailedVeraPdfPolicyChecks() {
		return failedVeraPdfPolicyChecks;
	}

	public void setFailedVeraPdfPolicyChecks(List<VeraPdfPolicyCheck> failedVeraPdfPolicyChecks) {
		this.failedVeraPdfPolicyChecks = failedVeraPdfPolicyChecks;
	}

	private void addFailedPolicyCheck(VeraPdfPolicyCheck newCheck) {
		for (VeraPdfPolicyCheck check : failedVeraPdfPolicyChecks) {
			if (check.getPolicyKey().equals(newCheck.getPolicyKey()) && areCheckDetailsEqual(check, newCheck))
				return;
		}
		this.failedVeraPdfPolicyChecks.add(newCheck);
	}

	private boolean areCheckDetailsEqual(VeraPdfPolicyCheck checkOne, VeraPdfPolicyCheck checkTwo) {
		if (checkOne.getDetails() == null && checkTwo.getDetails() == null) {
			return true;
		} else if (checkOne.getDetails() != null && checkTwo.getDetails() != null) {
			return checkOne.getDetails().equals(checkTwo.getDetails());
		} else
			return false;
	}

	public boolean isBuildInformation() {
		return buildInformation;
	}

	public void setBuildInformation(boolean buildInformation) {
		this.buildInformation = buildInformation;
	}

	public boolean isJobs() {
		return jobs;
	}

	public void setJobs(boolean jobs) {
		this.jobs = jobs;
	}

	public boolean isBatchSummary() {
		return batchSummary;
	}

	public void setBatchSummary(boolean batchSummary) {
		this.batchSummary = batchSummary;
	}

	public boolean isItem() {
		return item;
	}

	public void setItem(boolean item) {
		this.item = item;
	}

	public boolean isName() {
		return name;
	}

	public void setName(boolean name) {
		this.name = name;
	}

	public boolean isValidationReport() {
		return validationReport;
	}

	public void setValidationReport(boolean validationReport) {
		this.validationReport = validationReport;
	}

	public boolean isRule() {
		return rule;
	}

	public void setRule(boolean rule) {
		this.rule = rule;
	}

	public boolean isDescription() {
		return description;
	}

	public void setDescription(boolean description) {
		this.description = description;
	}

	public boolean isObject() {
		return object;
	}

	public void setObject(boolean object) {
		this.object = object;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public boolean isFailedCheck() {
		return failedCheck;
	}

	public void setFailedCheck(boolean failedCheck) {
		this.failedCheck = failedCheck;
	}

	public boolean isContext() {
		return context;
	}

	public void setContext(boolean context) {
		this.context = context;
	}

	public boolean isJob() {
		return job;
	}

	public void setJob(boolean job) {
		this.job = job;
	}

	public boolean isTaskResult() {
		return taskResult;
	}

	public void setTaskResult(boolean taskResult) {
		this.taskResult = taskResult;
	}

	public boolean isExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(boolean exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public boolean isPolicyReport() {
		return policyReport;
	}

	public void setPolicyReport(boolean policyReport) {
		this.policyReport = policyReport;
	}

	public boolean isFailedChecks() {
		return failedChecks;
	}

	public void setFailedChecks(boolean failedChecks) {
		this.failedChecks = failedChecks;
	}

	public VeraPdfRecord getVeraRecord() {
		return veraRecord;
	}

	public void setVeraRecord(VeraPdfRecord veraRecord) {
		this.veraRecord = veraRecord;
	}

	public VeraPdfAssertion getTempAssertion() {
		return tempAssertion;
	}

	public void setTempAssertion(VeraPdfAssertion tempAssertion) {
		this.tempAssertion = tempAssertion;
	}

	public VeraPdfPolicyCheck getTempCheck() {
		return tempCheck;
	}

	public void setTempCheck(VeraPdfPolicyCheck tempCheck) {
		this.tempCheck = tempCheck;
	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public static String getEncryptionExceptionKey() {
		return ENCRYPTION_EXCEPTION_KEY;
	}

}
