package de.unituebingen.validator.rest.representations;

import java.util.List;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.rest.resources.BatchReportResource;
import de.unituebingen.validator.rest.resources.BatchResource;
import de.unituebingen.validator.rest.resources.FileResource;

/**
 * Representation of a {@link Batch}.
 *
 */

@JsonIgnoreProperties(value = { "_links", "_embedded" }, ignoreUnknown = true, allowGetters = true)
public class BatchRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({ @InjectLink(value = BatchResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = BatchResource.PATH + "/${instance.id}/"
					+ FileResource.PATH, style = Style.ABSOLUTE, rel = LinkRelations.FILE_UPLOADS),
			@InjectLink(value = BatchReportResource.PATH
					+ "?batchId=${instance.id}", style = Style.ABSOLUTE, rel = LinkRelations.BATCH_REPORTS) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	private String description;
	private long creationDate;

	// Constructors

	public BatchRepresentation() {
		super();
	}

	@JsonCreator
	public BatchRepresentation(@JsonProperty("description") String description) {
		super();
		this.description = description;
	}

	// Getters and Setters

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
