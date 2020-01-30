package de.unituebingen.validator.persistence.model.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationProfile;
import de.unituebingen.validator.persistence.model.reports.checks.VeraPdfPolicyCheck;

@NamedQueries({
	@NamedQuery(name=VeraPdfRecord.GET_BY_ID, query="SELECT v from VeraPdfRecord v WHERE v.id = :id"),
	@NamedQuery(name=VeraPdfRecord.COUNT_ASSERTIONS_FOR_RECORD_WITH_ID, query="SELECT SIZE(v.assertions) from VeraPdfRecord v WHERE v.id = ?1"),
	@NamedQuery(name=VeraPdfRecord.SELECT_ASSERTIONS_FOR_RECORD_WTIH_ID, query="SELECT v.assertions from VeraPdfRecord v WHERE v.id = ?1")
})
@Entity
public class VeraPdfRecord extends Persistable{
		
	// Query constants
	public static final String GET_BY_ID = "VeraPdfRecord.getById";
	public static final String COUNT_ASSERTIONS_FOR_RECORD_WITH_ID = "VeraPdfRecord.countAssertions";
	public static final String SELECT_ASSERTIONS_FOR_RECORD_WTIH_ID = "VeraPdfRecord.selectAssertions";
	
	@OneToOne(cascade=CascadeType.MERGE)
	private FileValidationReport report;
	
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval=true)
	List<VeraPdfAssertion> assertions = new ArrayList<>();
	
	@Temporal(TemporalType.TIME)
	private Date start;
	
	@Temporal(TemporalType.TIME)
	private Date finish;
	
	@Enumerated(EnumType.STRING)
	private ValidationProfile validationProfile;
		
	private boolean exitedExceptionally;
	private String errorMessage;
	private boolean compliant;
	private boolean encrypted;
	private Long failedRules;
	private Long failedChecks;
	private Long passedChecks;
	private Long passedRules;
	private String reportFilePath;
	private Long failedPolicyChecks;
		
			
	public boolean isDeleted() {
		if(report != null) {
			return report.isDeleted();
		}
		return false;
	}
	
	// Setters and Getters
	
	public Boolean getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(Boolean encrypted) {
		this.encrypted = encrypted;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Long getFailedRules() {
		return failedRules;
	}

	public void setFailedRules(Long failedRules) {
		this.failedRules = failedRules;
	}

	public Long getFailedChecks() {
		return failedChecks;
	}

	public void setFailedChecks(Long failedChecks) {
		this.failedChecks = failedChecks;
	}

	public Long getPassedChecks() {
		return passedChecks;
	}

	public void setPassedChecks(Long passedChecks) {
		this.passedChecks = passedChecks;
	}

	public Long getPassedRules() {
		return passedRules;
	}

	public void setPassedRules(Long passedRules) {
		this.passedRules = passedRules;
	}

	public Boolean getCompliant() {
		return compliant;
	}

	public void setCompliant(Boolean compliant) {
		this.compliant = compliant;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getFinish() {
		return finish;
	}

	public void setFinish(Date finish) {
		this.finish = finish;
	}

	public ValidationProfile getValidationProfile() {
		return validationProfile;
	}

	public void setValidationProfile(ValidationProfile validationProfile) {
		this.validationProfile = validationProfile;
	}

	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public Long getFailedPolicyChecks() {
		return failedPolicyChecks;
	}

	public void setFailedPolicyChecks(Long failedPolicyChecks) {
		this.failedPolicyChecks = failedPolicyChecks;
	}

	public FileValidationReport getReport() {
		return report;
	}

	public void setReport(FileValidationReport report) {
		this.report = report;
	}

	public boolean hasExitedExceptionally() {
		return exitedExceptionally;
	}

	public void setExitedExceptionally(boolean exitedExceptionally) {
		this.exitedExceptionally = exitedExceptionally;
	}

	public List<VeraPdfAssertion> getAssertions() {
		return assertions;
	}

	public void setAssertions(List<VeraPdfAssertion> assertions) {
		this.assertions = assertions;
	}
	
	
	
}
