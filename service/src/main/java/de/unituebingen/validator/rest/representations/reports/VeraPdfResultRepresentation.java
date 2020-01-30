package de.unituebingen.validator.rest.representations.reports;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.ValidationProfile;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.FileReportResource;
import de.unituebingen.validator.rest.resources.VeraPdfResultsResource;

public class VeraPdfResultRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = VeraPdfResultsResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = VeraPdfResultsResource.PATH
					+ "/${instance.id}/assertions", style = Style.ABSOLUTE, rel = LinkRelations.VERAPDF_ASSERTIONS),
			@InjectLink(value = FileReportResource.PATH
					+ "/${instance.fileReportId}", style = Style.ABSOLUTE, rel = LinkRelations.FILE_REPORT) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long fileReportId;

	private boolean executionError;
	private ValidationProfile validationProfile;
	private Boolean compliant;
	private Boolean encrypted;
	private Long failedRules;
	private Long failedChecks;
	private Long passedChecks;
	private Long passedRules;
	private Long failedPolicyChecks;
	private String errorMessage;

	// Setters and Getters

	public long getFileReportId() {
		return fileReportId;
	}

	public void setFileReportId(long fileReportId) {
		this.fileReportId = fileReportId;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public ValidationProfile getValidationProfile() {
		return validationProfile;
	}

	public void setValidationProfile(ValidationProfile validationProfile) {
		this.validationProfile = validationProfile;
	}

	public Boolean getCompliant() {
		return compliant;
	}

	public void setCompliant(Boolean compliant) {
		this.compliant = compliant;
	}

	public Boolean getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(Boolean ecrypted) {
		this.encrypted = ecrypted;
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

	public Long getFailedPolicyChecks() {
		return failedPolicyChecks;
	}

	public void setFailedPolicyChecks(Long failedPolicyChecks) {
		this.failedPolicyChecks = failedPolicyChecks;
	}

	public boolean isExecutionError() {
		return executionError;
	}

	public void setExecutionError(boolean executionError) {
		this.executionError = executionError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
