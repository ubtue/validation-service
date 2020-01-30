package de.unituebingen.validator.rest.representations;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.rest.resources.ConfigurationResource;
import de.unituebingen.validator.rest.resources.FileNameRuleResource;
import de.unituebingen.validator.rest.resources.FitsResultRuleResource;

/**
 * Representation of a {@link ProcessorConfiguration}
 *
 */
@JsonIgnoreProperties(value = { "_links" }, ignoreUnknown = true, allowGetters = true)
public class ConfigurationRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.id}/"
					+ FileNameRuleResource.PATH, style = Style.ABSOLUTE, rel = LinkRelations.FILE_NAME_RULES),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.id}/"
					+ FitsResultRuleResource.PATH, style = Style.ABSOLUTE, rel = LinkRelations.FITS_RESULT_RULES),
			@InjectLink(value = ConfigurationResource.PATH + "/${instance.id}/"
					+ ConfigurationResource.PATH_VERAPDF_SETUP, style = Style.ABSOLUTE, rel = LinkRelations.VERAPDF_SETUP) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	private String publicIdentifier;
	private long creationDate;
	private long fitsTimeOut;
	private long veraPdfTimeOut;
	private long veraPdfMaxHeapSize;
	private boolean fitsEnabled;
	private String description;

	// Setters and Getters

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public boolean isFitsEnabled() {
		return fitsEnabled;
	}

	public void setFitsEnabled(boolean fitsEnabled) {
		this.fitsEnabled = fitsEnabled;
	}

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

	public long getFitsTimeOut() {
		return fitsTimeOut;
	}

	public void setFitsTimeOut(long fitsTimeOut) {
		this.fitsTimeOut = fitsTimeOut;
	}

	public long getVeraPdfTimeOut() {
		return veraPdfTimeOut;
	}

	public void setVeraPdfTimeOut(long veraPdfTimeOut) {
		this.veraPdfTimeOut = veraPdfTimeOut;
	}

	public long getVeraPdfMaxHeapSize() {
		return veraPdfMaxHeapSize;
	}

	public void setVeraPdfMaxHeapSize(long veraPdfMaxHeapSize) {
		this.veraPdfMaxHeapSize = veraPdfMaxHeapSize;
	}

	public String getPublicIdentifier() {
		return publicIdentifier;
	}

	public void setPublicIdentifier(String publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

}
