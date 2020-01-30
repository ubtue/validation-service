package de.unituebingen.validator.persistence.model.configuration.rules;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;

@Entity(name="VALIDATION_RULE")
@NamedQueries ({
	@NamedQuery(name = Rule.SELECT_ALL_WITH_TYPE_FOR_CONFIG_ID_ORDER_BY_LAST_MODIFIED_DESC, 
				query = "SELECT v FROM VALIDATION_RULE v WHERE TYPE(v) IN (?1) AND v.configuration.id = ?2 ORDER BY v.lastModified DESC"
	),
	@NamedQuery(name = Rule.COUNT_ALL_WITH_TYPE_FOR_CONFIG_ID, 
				query = "SELECT COUNT(v) FROM VALIDATION_RULE v WHERE TYPE(v) IN (?1) AND v.configuration.id = ?2"
	),
	@NamedQuery(name = Rule.SELECT_RULES_BY_ID_AND_TYPE, 
				query = "SELECT v FROM VALIDATION_RULE v WHERE v.id = ?1 AND TYPE(v) IN (?2)")
})
public class Rule extends Persistable{
	
	// Query constants
	public static final String SELECT_ALL_WITH_TYPE_FOR_CONFIG_ID_ORDER_BY_LAST_MODIFIED_DESC = "Rule.selectAllWithTypeForConfigIdOrderByLastModifiedDesc";
	public static final String COUNT_ALL_WITH_TYPE_FOR_CONFIG_ID = "Rule.countAllWithTypeForConfigId";
	public static final String SELECT_RULES_BY_ID_AND_TYPE = "Rule.selectRulesByIdAndType";
	

	@ManyToOne(cascade={CascadeType.MERGE})
	private ProcessorConfiguration configuration;
	
	@ElementCollection
	Map<String, String> messageTranslations = new HashMap<>();
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModified;
	
	@NotNull 
	@Enumerated(EnumType.STRING)
	private ValidationOutcome outcome; 
	
	private String name;
	
	
	@PreUpdate
	@PrePersist
	private void setLastModified() {
		this.lastModified = new Date();
	}
	
	@PreRemove
	public void cleanUp() {
		if(this.configuration != null)
			this.configuration.deleteRule(this);
	}
	
	
	// Generated Setters and Getters
	
	public Map<String, String> getMessageTranslations() {
		return messageTranslations;
	}

	public void setMessageTranslations(Map<String, String> messageTranslations) {
		this.messageTranslations = messageTranslations;
	}

	public ValidationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ValidationOutcome outcome) {
		this.outcome = outcome;
	}

	public ProcessorConfiguration getConfiguration() {
		return configuration;
	}

	public void internalSetConfiguration(ProcessorConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastModified() {
		return lastModified;
	}


}
