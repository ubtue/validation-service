package de.unituebingen.validator.rest.representations;

import java.util.List;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unituebingen.validator.rest.resources.UsersResource;

public class UsersRepresentation extends CollectionRepresentation {

	@InjectLinks({
			@InjectLink(value = UsersResource.PATH + FIRST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "first", condition = FIRST_PAGE_CONDITION),
			@InjectLink(value = UsersResource.PATH + PREVIOUS_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "previous", condition = PREVIOUS_PAGE_CONDITION),
			@InjectLink(value = UsersResource.PATH + SELF_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = UsersResource.PATH + NEXT_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "next", condition = NEXT_PAGE_CONDITION),
			@InjectLink(value = UsersResource.PATH + LAST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "last", condition = LAST_PAGE_CONDITION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	public UsersRepresentation(long totalCount, int count, int pageSize, int pageIndex) {
		super(totalCount, count, pageSize, pageIndex);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
