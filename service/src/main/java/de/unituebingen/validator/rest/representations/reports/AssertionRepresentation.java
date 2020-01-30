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

import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertionStatus;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.VeraPdfResultsResource;

public class AssertionRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({ @InjectLink(value = VeraPdfResultsResource.PATH
			+ "/${instance.veraResultId}/assertions/${instance.id}", style = Style.ABSOLUTE, rel = "self"), })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long veraResultId;

	private VeraPdfAssertionStatus status;
	private String description;
	private List<String> contexts = new ArrayList<>();
	private String specification;
	private String clause;
	private int testNumber;
	private String object;
	private String test;
	private int occurences;
	private Long passedChecks;
	private Long failedChecks;

	// Setters and Getters

	public VeraPdfAssertionStatus getStatus() {
		return status;
	}

	public void setStatus(VeraPdfAssertionStatus status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getContexts() {
		return contexts;
	}

	public void setContexts(List<String> contexts) {
		this.contexts = contexts;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getClause() {
		return clause;
	}

	public void setClause(String clause) {
		this.clause = clause;
	}

	public int getTestNumber() {
		return testNumber;
	}

	public void setTestNumber(int testNumber) {
		this.testNumber = testNumber;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}

	public Long getPassedChecks() {
		return passedChecks;
	}

	public void setPassedChecks(Long passedChecks) {
		this.passedChecks = passedChecks;
	}

	public Long getFailedChecks() {
		return failedChecks;
	}

	public void setFailedChecks(Long failedChecks) {
		this.failedChecks = failedChecks;
	}

	public long getVeraResultId() {
		return veraResultId;
	}

	public void setVeraResultId(long veraResultId) {
		this.veraResultId = veraResultId;
	}

}
