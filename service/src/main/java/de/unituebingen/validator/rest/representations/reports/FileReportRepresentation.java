package de.unituebingen.validator.rest.representations.reports;

import java.util.ArrayList;
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

import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.reports.ExecutionOutcome;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.BatchReportResource;
import de.unituebingen.validator.rest.resources.ChecksResource;
import de.unituebingen.validator.rest.resources.FileReportResource;
import de.unituebingen.validator.rest.resources.FileResource;
import de.unituebingen.validator.rest.resources.FitsResultsResource;
import de.unituebingen.validator.rest.resources.VeraPdfResultsResource;

public class FileReportRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = FileReportResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = BatchReportResource.PATH
					+ "/${instance.batchReportId}", style = Style.ABSOLUTE, rel = LinkRelations.BATCH_REPORT),
			@InjectLink(value = FileResource.PATH
					+ "/${instance.fileId}", style = Style.ABSOLUTE, rel = LinkRelations.FILE_UPLOAD, condition = "${instance.fileId != 0}"),
			@InjectLink(value = ChecksResource.PATH
					+ "?fileReportId=${instance.id}", style = Style.ABSOLUTE, rel = LinkRelations.CHECKS),
			@InjectLink(value = VeraPdfResultsResource.PATH
					+ "/${instance.veraPdfReportId}", style = Style.ABSOLUTE, rel = LinkRelations.VERAPDF_RESULT, condition = "${instance.veraPdfReportId != 0}"),
			@InjectLink(value = FitsResultsResource.PATH
					+ "?fileReportId=${instance.id}", style = Style.ABSOLUTE, rel = LinkRelations.FITS_RESULTS) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	private ValidationOutcome validationOutcome;
	private ExecutionOutcome fitsExecutionOutcome;
	private ExecutionOutcome veraPdfExecutionOutcome;

	@XmlTransient
	@JsonIgnore
	private long fileId;

	@XmlTransient
	@JsonIgnore
	private long batchReportId;

	@XmlTransient
	@JsonIgnore
	private long veraPdfReportId;

	private int failedNameChecks;
	private int failedFitsChecks;
	private List<String> errorMessages = new ArrayList<>();

	// Setters and Getters

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public long getBatchReportId() {
		return batchReportId;
	}

	public void setBatchReportId(long batchReportId) {
		this.batchReportId = batchReportId;
	}

	public ValidationOutcome getValidationOutcome() {
		return validationOutcome;
	}

	public void setValidationOutcome(ValidationOutcome validationOutcome) {
		this.validationOutcome = validationOutcome;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public long getVeraPdfReportId() {
		return veraPdfReportId;
	}

	public void setVeraPdfReportId(long veraPdfReportId) {
		this.veraPdfReportId = veraPdfReportId;
	}

	public int getFailedNameChecks() {
		return failedNameChecks;
	}

	public void setFailedNameChecks(int failedNameChecks) {
		this.failedNameChecks = failedNameChecks;
	}

	public int getFailedFitsChecks() {
		return failedFitsChecks;
	}

	public void setFailedFitsChecks(int failedFitsChecks) {
		this.failedFitsChecks = failedFitsChecks;
	}

	public ExecutionOutcome getFitsExecutionOutcome() {
		return fitsExecutionOutcome;
	}

	public void setFitsExecutionOutcome(ExecutionOutcome fitsExecutionOutcome) {
		this.fitsExecutionOutcome = fitsExecutionOutcome;
	}

	public ExecutionOutcome getVeraPdfExecutionOutcome() {
		return veraPdfExecutionOutcome;
	}

	public void setVeraPdfExecutionOutcome(ExecutionOutcome veraPdfExecutionOutcome) {
		this.veraPdfExecutionOutcome = veraPdfExecutionOutcome;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

}
