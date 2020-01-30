package de.unituebingen.validator.rest.representations.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
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
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.ConfigurationResource;
import de.unituebingen.validator.rest.resources.FileNameRuleResource;

public class FileNameRuleRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = FileNameRuleResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH
					+ "/${instance.configurationId}", style = Style.ABSOLUTE, rel = LinkRelations.CONFIGURATION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long configurationId;

	private FileNameValidationType type;

	private String value;

	private String ruleName;

	private ValidationOutcome outcome;

	@ElementCollection
	@JsonProperty("translations")
	Map<String, String> messageTranslations = new HashMap<>();

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

	public FileNameValidationType getType() {
		return type;
	}

	public void setType(FileNameValidationType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ValidationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ValidationOutcome outcome) {
		this.outcome = outcome;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Map<String, String> getMessageTranslations() {
		return messageTranslations;
	}

	public void setMessageTranslations(Map<String, String> messageTranslations) {
		this.messageTranslations = messageTranslations;
	}

}
