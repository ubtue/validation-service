package de.unituebingen.validator.rest.representations;

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

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.rest.resources.BatchResource;
import de.unituebingen.validator.rest.resources.FileResource;

/**
 * Represents a result page when querying {@link FileUpload}s.
 *
 */
public class FilesRepresentation extends CollectionRepresentation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = BatchResource.PATH + "/${instance.batchId}/" + FileResource.PATH + FIRST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "first", condition = FIRST_PAGE_CONDITION),
			@InjectLink(value = BatchResource.PATH + "/${instance.batchId}/" + FileResource.PATH
					+ PREVIOUS_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "previous", condition = PREVIOUS_PAGE_CONDITION),
			@InjectLink(value = BatchResource.PATH + "/${instance.batchId}/" + FileResource.PATH + SELF_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = BatchResource.PATH + "/${instance.batchId}/" + FileResource.PATH + NEXT_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "next", condition = NEXT_PAGE_CONDITION),
			@InjectLink(value = BatchResource.PATH + "/${instance.batchId}/" + FileResource.PATH + LAST_PAGE_PARAMETER
					+ OTHER_PARAMETERS, style = Style.ABSOLUTE, rel = "last", condition = LAST_PAGE_CONDITION) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long batchId;

	/**
	 * Constructor for creating the batches representation.
	 * 
	 * @param totalCount
	 *            The amount of results in total.
	 * @param pageIndex
	 *            The index of the page.
	 * @param pageSize
	 *            The number of results per page in general.
	 * @param count
	 *            The number of uploads to be embedded in this page.
	 */
	public FilesRepresentation(long totalCount, int pageIndex, int pageSize, int count) {
		super(totalCount, count, pageSize, pageIndex);
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
