package de.unituebingen.validator.rest.representations.rules;

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
import de.unituebingen.validator.rest.representations.CollectionRepresentation;
import de.unituebingen.validator.rest.resources.ConfigurationResource;
import de.unituebingen.validator.rest.resources.FileNameRuleResource;

public class FileNameRulesRepresentation extends CollectionRepresentation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/" + FileNameRuleResource.PATH
					+ FIRST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "first", condition = FIRST_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/" + FileNameRuleResource.PATH
					+ PREVIOUS_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "previous", condition = PREVIOUS_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/" + FileNameRuleResource.PATH
					+ SELF_PAGE_PARAMETER + OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/" + FileNameRuleResource.PATH
					+ NEXT_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "next", condition = NEXT_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.configurationId}/" + FileNameRuleResource.PATH
					+ LAST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "last", condition = LAST_PAGE_CONDITION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long configurationId;

	public FileNameRulesRepresentation(long totalCount, int page, int pageSize, int count) {
		super(totalCount, count, pageSize, page);
	}

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

}
