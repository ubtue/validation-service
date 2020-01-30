package de.unituebingen.validator.rest.representations;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Abstract representation.
 * 
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({ "links", "embedded", "id" })
public abstract class Representation {

	protected Long id;

	@JsonProperty("_embedded")
	protected Map<String, Object> embedded = new HashMap<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, Object> getEmbedded() {
		return embedded;
	}

	public void setEmbedded(Map<String, Object> embedded) {
		this.embedded = embedded;
	}

}
