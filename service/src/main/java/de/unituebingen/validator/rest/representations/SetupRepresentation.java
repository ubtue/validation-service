package de.unituebingen.validator.rest.representations;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;
import de.unituebingen.validator.persistence.model.configuration.MessageTranslations;
import de.unituebingen.validator.rest.resources.SetupResource;

/**
 * Representation for {@link GlobalSettings}
 *
 */
public class SetupRepresentation extends Representation {

	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	@InjectLinks({ @InjectLink(value = SetupResource.PATH, style = Style.ABSOLUTE, rel = "self") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@JsonProperty("_links")
	List<Link> links;

	private long parallelTasks;
	private long threadsPerTask;
	private int pageSize;
	private MessageTranslations messageTranslations;
	private int autoDeleteHours;
	private boolean autoDeleteBatches;

	// Getters and Setters

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public long getParallelTasks() {
		return parallelTasks;
	}

	public void setParallelTasks(long parallelTasks) {
		this.parallelTasks = parallelTasks;
	}

	public long getThreadsPerTask() {
		return threadsPerTask;
	}

	public void setThreadsPerTask(long threadsPerTask) {
		this.threadsPerTask = threadsPerTask;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public MessageTranslations getMessageTranslations() {
		return messageTranslations;
	}

	public void setMessageTranslations(MessageTranslations messageTranslations) {
		this.messageTranslations = messageTranslations;
	}

	public int getAutoDeleteHours() {
		return autoDeleteHours;
	}

	public void setAutoDeleteHours(int autoDeleteHours) {
		this.autoDeleteHours = autoDeleteHours;
	}

	public boolean isAutoDeleteBatches() {
		return autoDeleteBatches;
	}

	public void setAutoDeleteBatches(boolean autoDeleteBatches) {
		this.autoDeleteBatches = autoDeleteBatches;
	}

}
