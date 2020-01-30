package de.unituebingen.validator.persistence.model.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.configuration.rules.Rule;

@Entity
@NamedQueries ({
	@NamedQuery(name = ProcessorConfiguration.SELECT_ALL_BY_CREATION_DATE_DESC, 
		query = "SELECT p from ProcessorConfiguration p ORDER BY p.creationDate DESC"
	),
	@NamedQuery(name = ProcessorConfiguration.COUNT_ALL_WITH_DESCRIPTION_PATTERN, 
		query = "SELECT COUNT(p) from ProcessorConfiguration p  WHERE UPPER(p.description) LIKE UPPER(?1)"
	),
	@NamedQuery(name = ProcessorConfiguration.SELECT_BY_PUBLIC_IDENTIFIER, 
		query = "SELECT p from ProcessorConfiguration p  WHERE p.publicIdentifier=?1"
	)
})
public class ProcessorConfiguration extends Persistable{
	// Query constants
	public static final String SELECT_BY_PUBLIC_IDENTIFIER = "ProcessorConfiguration.selectByPublicIdentifier";
	public static final String SELECT_ALL_BY_CREATION_DATE_DESC = "ProcessorConfiguration.selectAllByCreationDateDesc";
	public static final String COUNT_ALL_WITH_DESCRIPTION_PATTERN = "Batch.countAllWithDescriptionPattern";
				
	@OneToOne(orphanRemoval=true, cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private VeraPdfConfiguration veraPdfConfiguration;
	
	@OneToMany(orphanRemoval=true, cascade={CascadeType.MERGE, CascadeType.PERSIST}, mappedBy="configuration")
	private List<Rule> rules = new ArrayList<>();
	
	@OneToMany(orphanRemoval=false, cascade={CascadeType.MERGE, CascadeType.PERSIST}, mappedBy="processorConfiguration")
	private List<ValidationTask> validationTasks = new ArrayList<>();
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(nullable=false, unique=true, length=1200)
	private String publicIdentifier;
	
	private String description;	
	private boolean fitsActive;
	private long fitsTimeOut = 120;
	private long veraPdfTimeOut = 120;
	private long veraPdfMaxHeapSize = 256;
	
		
	public ProcessorConfiguration() {
		super();
		this.publicIdentifier = UUID.randomUUID().toString();
	}

	public void preRemove() {
		for (Iterator<ValidationTask> iterator = validationTasks.iterator(); iterator.hasNext();) {
			ValidationTask task = (ValidationTask) iterator.next();	
			task.internalSetProcessorConfiguration(null);
			iterator.remove();
		}
		
		for (Iterator<Rule> iterator = rules.iterator(); iterator.hasNext();) {
			Rule rule = (Rule) iterator.next();
			rule.internalSetConfiguration(null);
			iterator.remove();
		}
		
		this.veraPdfConfiguration = null;
	}
	
	public List<Rule> getRules() {
		return rules;
	}

	public void addRules(List<? extends Rule> rules) {
		for (Rule rule : rules) {
			rule.internalSetConfiguration(this);
			if(!this.rules.contains(rule))
				this.rules.add(rule);
		}
	}
	
	public void addRule(Rule rule) {
		rule.internalSetConfiguration(this);
		if(!this.rules.contains(rule))
			this.rules.add(rule);
	}
	
	public void deleteRule(Rule rule) {
		rule.internalSetConfiguration(null);
		this.rules.remove(rule);
	}
	
	public VeraPdfConfiguration getVeraPdfConfiguration() {
		return veraPdfConfiguration;
	}

	public void setVeraPdfConfiguration(VeraPdfConfiguration veraPdfConfiguration) {
		this.veraPdfConfiguration = veraPdfConfiguration;
		veraPdfConfiguration.setConfiguration(this);
	}

	public void addValidationTask(ValidationTask task) {
		if(!validationTasks.contains(task)) {
			validationTasks.add(task);
		}
		task.internalSetProcessorConfiguration(this);
	}
	
	public void removeValidationTask(ValidationTask task) {
		validationTasks.remove(task);
		task.internalSetProcessorConfiguration(null);
	}
	
	public boolean isFitsActive() {
		return fitsActive;
	}

	public void setFitsActive(boolean fitsActive) {
		this.fitsActive = fitsActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
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
