package de.unituebingen.validator.persistence.model.configuration;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import de.unituebingen.validator.persistence.model.Persistable;

/**
 * Global application settings
 * @author Fabian Hamm
 *
 */
@Entity
@NamedQueries ({
	@NamedQuery(name = GlobalSettings.GET_GLOBAL_SETTINGS, query = "SELECT g FROM GlobalSettings g")
})
public class GlobalSettings extends Persistable{
	
	public static final String GET_GLOBAL_SETTINGS = "GlobalSettings.get";

	private long concurrentTasks;
	private long concurrenThreadsPerTask;
	private int pageSize;
	private int embeddedResources;
	
	@Embedded
	private MessageTranslations messageTranslations;
	
	/** The amount of hours to wait until a batch gets deleted automatically */
	private int autoDeleteHours;
	private boolean autoDeleteBatches;
	
	

	// Setters and Getters

	public long getConcurrentTasks() {
		return concurrentTasks;
	}
	
	public void setConcurrentTasks(long concurrentTasks) {
		this.concurrentTasks = concurrentTasks;
	}
	
	public long getConcurrenThreadsPerTask() {
		return concurrenThreadsPerTask;
	}
	
	public void setConcurrenThreadsPerTask(long concurrenThreadsPerTask) {
		this.concurrenThreadsPerTask = concurrenThreadsPerTask;
	}
	
	public static String getGetGlobalSettings() {
		return GET_GLOBAL_SETTINGS;
	}
	
	public int getEmbeddedResources() {
		return embeddedResources;
	}
	
	public void setEmbeddedResources(int embeddedResources) {
		this.embeddedResources = embeddedResources;
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
