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

import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.Representation;
import de.unituebingen.validator.rest.resources.FileReportResource;
import de.unituebingen.validator.rest.resources.FitsResultsResource;

public class FitsResultRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({
			@InjectLink(value = FitsResultsResource.PATH + "/${instance.id}", style = Style.ABSOLUTE, rel = "self"),
			@InjectLink(value = FileReportResource.PATH
					+ "/${instance.fileReportId}", style = Style.ABSOLUTE, rel = LinkRelations.FILE_REPORT) })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	@XmlTransient
	@JsonIgnore
	private long fileReportId;

	private String tool;
	private String toolVersion;
	private String formatName;
	private String puid;
	private String mime;
	private Boolean wellFormed;
	private Boolean valid;

	// Setters and Getters

	public long getFileReportId() {
		return fileReportId;
	}

	public void setFileReportId(long fileReportId) {
		this.fileReportId = fileReportId;
	}

	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public Boolean getWellFormed() {
		return wellFormed;
	}

	public void setWellFormed(Boolean wellFormed) {
		this.wellFormed = wellFormed;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
