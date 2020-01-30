package de.unituebingen.validator.persistence.model.reports;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Table;

import de.unituebingen.validator.persistence.model.Persistable;



@Entity
@Table(name="PdfaAssertion")
public class VeraPdfAssertion extends Persistable{
		
	@Enumerated(EnumType.STRING)
	private VeraPdfAssertionStatus status;
	
	@Column(length=1700)
	private String description;
	
	@ElementCollection
	@Basic(fetch=FetchType.EAGER)
	@Column(length=1700)
	private List<String> contexts = new ArrayList<>();
	
	@Column(length=1700)
	private String test;	
	
	private String specification;
	private String clause;
	private int testNumber;
	private String object;
	private int occurences;
	private Long passedChecks;
	private Long failedChecks;
	
	

	public VeraPdfAssertionStatus getStatus() {
		return status;
	}

	public void setStatus(VeraPdfAssertionStatus status) {
		this.status = status;
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

	public String getMessage() {
		return description;
	}

	public void setMessage(String message) {
		this.description = message;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}

	public  void incrementOccurences() {
		this.occurences++;
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

	public List<String> getContexts() {
		return contexts;
	}

	public void setContexts(List<String> locations) {
		this.contexts = locations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

		
}

