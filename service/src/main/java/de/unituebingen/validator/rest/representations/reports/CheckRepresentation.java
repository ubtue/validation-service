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

import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.ChecksResource;
import de.unituebingen.validator.rest.resources.FileReportResource;

public class CheckRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({ @InjectLink(value = ChecksResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = FileReportResource.PATH
					+ "/${instance.reportId}", style = Style.ABSOLUTE, rel = LinkRelations.FILE_REPORT) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long reportId;

	private ValidationOutcome outcome;
	private String test;
	private String resultMessage;
	private CheckType checkType;

	// Setters and Getters

	public ValidationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ValidationOutcome outcome) {
		this.outcome = outcome;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public CheckType getCheckType() {
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

}
