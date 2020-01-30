package de.unituebingen.validator.rest.representations;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.rest.resources.ConfigurationResource;

/**
 * Represents a result page when querying for {@link ProcessorConfiguration}s.
 *
 */
public class ConfigurationsRepresentation extends CollectionRepresentation {

	@InjectLinks({
			@InjectLink(value = ConfigurationResource.PATH + FIRST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "first", condition = FIRST_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + PREVIOUS_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "previous", condition = PREVIOUS_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + SELF_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH + NEXT_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "next", condition = NEXT_PAGE_CONDITION),
			@InjectLink(value = ConfigurationResource.PATH + LAST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "last", condition = LAST_PAGE_CONDITION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	/**
	 * Constructor for creating the configurations representation.
	 * 
	 * @param totalCount
	 *            The amount of results in total.
	 * @param pageIndex
	 *            The index of the page.
	 * @param pageSize
	 *            The number of results per page in general.
	 * @param count
	 *            The number of configurations to be embedded.
	 */
	public ConfigurationsRepresentation(long totalCount, int pageIndex, int pageSize, int count) {
		super(totalCount, count, pageSize, pageIndex);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
