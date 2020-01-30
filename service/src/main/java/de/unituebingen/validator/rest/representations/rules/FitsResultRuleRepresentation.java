package de.unituebingen.validator.rest.representations.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
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
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationType;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.ConfigurationResource;
import de.unituebingen.validator.rest.resources.FitsResultRuleResource;

public class FitsResultRuleRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = FitsResultRuleResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH
					+ "/${instance.configurationId}", style = Style.ABSOLUTE, rel = LinkRelations.CONFIGURATION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long configurationId;

	private FitsRecordValidationType type;
	private String mime;
	private String puid;
	private String extension;
	private ValidationOutcome outcome;
	private String toolName;
	private String ruleName;

	@NotNull
	private ValidationOutcome outcomeOnMissingFitsRecord;

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

	public FitsRecordValidationType getType() {
		return type;
	}

	public void setType(FitsRecordValidationType type) {
		this.type = type;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public ValidationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ValidationOutcome outcome) {
		this.outcome = outcome;
	}

	public ValidationOutcome getOutcomeOnMissingFitsRecord() {
		return outcomeOnMissingFitsRecord;
	}

	public void setOutcomeOnMissingFitsRecord(ValidationOutcome outcomeOnMissingFitsRecord) {
		this.outcomeOnMissingFitsRecord = outcomeOnMissingFitsRecord;
	}

	public Map<String, String> getMessageTranslations() {
		return messageTranslations;
	}

	public void setMessageTranslations(Map<String, String> messageTranslations) {
		this.messageTranslations = messageTranslations;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

}
