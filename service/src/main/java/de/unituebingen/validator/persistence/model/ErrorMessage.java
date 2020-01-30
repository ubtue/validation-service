package de.unituebingen.validator.persistence.model;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an error message.
 *
 */
@Embeddable
@XmlRootElement(name = "errorMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessage {

	private String message;
	private String type;

	public ErrorMessage() {
		super();
	}

	public ErrorMessage(String message, String type) {
		super();
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
