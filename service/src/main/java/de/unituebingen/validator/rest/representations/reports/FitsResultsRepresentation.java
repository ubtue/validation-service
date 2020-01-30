package de.unituebingen.validator.rest.representations.reports;

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
import de.unituebingen.validator.rest.resources.FitsResultsResource;

public class FitsResultsRepresentation extends CollectionRepresentation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = FitsResultsResource.PATH + FIRST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "first", condition = FIRST_PAGE_CONDITION),
			@InjectLink(value = FitsResultsResource.PATH + PREVIOUS_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "previous", condition = PREVIOUS_PAGE_CONDITION),
			@InjectLink(value = FitsResultsResource.PATH + SELF_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = FitsResultsResource.PATH + NEXT_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "next", condition = NEXT_PAGE_CONDITION),
			@InjectLink(value = FitsResultsResource.PATH + LAST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "last", condition = LAST_PAGE_CONDITION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long fileReportId;

	public FitsResultsRepresentation(long totalCount, int page, int pageSize, int count) {
		super(totalCount, count, pageSize, page);
	}

}
