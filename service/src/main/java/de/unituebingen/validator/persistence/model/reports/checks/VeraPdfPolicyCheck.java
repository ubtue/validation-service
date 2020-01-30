package de.unituebingen.validator.persistence.model.reports.checks;

import javax.persistence.Entity;

@Entity
public class VeraPdfPolicyCheck extends Check{
		
	private String policyKey;
	private String details;
	private String test;
	private String location;

	public String getPolicyKey() {
		return policyKey;
	}

	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
	
	
}
