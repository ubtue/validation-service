package de.unituebingen.validator.rest.representations.reports;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BatchReportOrderRepresentation {

	private String configurationIdentifier;
	private long batchId;

	// Constructors

	public BatchReportOrderRepresentation(String configurationIdentifier, long batchId) {
		super();
		this.configurationIdentifier = configurationIdentifier;
		this.batchId = batchId;
	}

	public BatchReportOrderRepresentation() {
		super();
	}

	// Setters and Getters

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getConfigurationIdentifier() {
		return configurationIdentifier;
	}

	public void setConfigurationIdentifier(String configurationIdentifier) {
		this.configurationIdentifier = configurationIdentifier;
	}

}
