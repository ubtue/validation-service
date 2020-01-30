package de.unituebingen.validator.rest.representations.reports;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.BatchReportResource;
import de.unituebingen.validator.rest.resources.BatchResource;
import de.unituebingen.validator.rest.resources.ConfigurationResource;
import de.unituebingen.validator.rest.resources.FileReportResource;

public class BatchReportRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = BatchReportResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = BatchResource.PATH
					+ "/${instance.batchId}", style = Style.ABSOLUTE, rel = LinkRelations.BATCH),
			@InjectLink(value = ConfigurationResource.PATH
					+ "/${instance.configurationId}", style = Style.ABSOLUTE, rel = LinkRelations.CONFIGURATION, condition = "${instance.configurationId!=0}"),
			@InjectLink(value = FileReportResource.PATH
					+ "?batchReportId=${instance.id}", style = Style.ABSOLUTE, rel = LinkRelations.FILE_REPORTS) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long batchId;

	@XmlTransient
	@JsonIgnore
	private long configurationId;

	@Enumerated(EnumType.STRING)
	private ValidationStatus status;

	private BatchResultSummaryRepresentation summary;

	private long creationDate;

	private long finishedDate;

	// Setters and Getters

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(long configurationId) {
		this.configurationId = configurationId;
	}

	public ValidationStatus getStatus() {
		return status;
	}

	public void setStatus(ValidationStatus status) {
		this.status = status;
	}

	public BatchResultSummaryRepresentation getSummary() {
		return summary;
	}

	public void setSummary(BatchResultSummaryRepresentation summary) {
		this.summary = summary;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(long finishedDate) {
		this.finishedDate = finishedDate;
	}

}
