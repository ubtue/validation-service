package de.unituebingen.validator.rest.representations;

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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.ValidationProfile;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfExecutionMode;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfPolicyOptions;
import de.unituebingen.validator.rest.resources.ConfigurationResource;

/**
 * Representation of a {@link VeraPdfConfiguration}.
 *
 */
@JsonIgnoreProperties(value = { "_embedded" }, ignoreUnknown = true, allowGetters = false)
public class VeraPdfConfigurationRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/"
					+ ConfigurationResource.PATH_VERAPDF_SETUP, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH
					+ "/${instance.configurationId}", style = Style.ABSOLUTE, rel = LinkRelations.CONFIGURATION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long configurationId;

	private VeraPdfExecutionMode executionMode;
	private ValidationProfile validationProfile = ValidationProfile.UNSPECIFIED;
	private boolean reportPassedRules;
	private int failedChecksThreshold = 100;
	private int failedChecksPerRuleDisplayed = 5;
	private boolean failOnInvalidPdfA;
	private VeraPdfPolicyOptions pdfPolicies;

	public VeraPdfConfigurationRepresentation() {
		super();
	}

	// Setters and Getters

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(long configurationId) {
		this.configurationId = configurationId;
	}

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

	public VeraPdfPolicyOptions getPdfPolicies() {
		return pdfPolicies;
	}

	public void setPdfPolicies(VeraPdfPolicyOptions pdfPolicies) {
		this.pdfPolicies = pdfPolicies;
	}

}
