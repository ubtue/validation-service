package de.unituebingen.validator.persistence.model.reports.checks;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import de.unituebingen.validator.persistence.model.Persistable;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;

@NamedQueries({
	@NamedQuery(name=Check.SELECT_FAILED_OF_TYPE_BY_ID_ASC, query="SELECT c from RULECHECK c WHERE"
		+ " c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID AND TYPE(c) IN ?1 AND c.report.task.deleted=false ORDER BY c.id ASC"
	),
	@NamedQuery(name=Check.SELECT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID_BY_ID_ASC, query="SELECT c from RULECHECK c WHERE"
		+ " c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID AND c.report.id = ?1 AND c.report.task.deleted = false AND TYPE(c) IN ?2 ORDER BY c.id ASC"
	),
	@NamedQuery(name=Check.SELECT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID_BY_ID_ASC, query="SELECT c FROM RULECHECK c WHERE (c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID"
		+ " AND c.report.task.id = ?1 AND c.report.task.deleted = false) AND TYPE(c) IN ?2 ORDER BY c.id ASC"
	),
	@NamedQuery(name=Check.COUNT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID, query="SELECT Count(c) from RULECHECK c WHERE"
		+ " c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID AND TYPE(c) IN ?1 AND c.report.id = ?2 AND c.report.task.deleted = false"
	),
	@NamedQuery(name=Check.COUNT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID, query="SELECT Count(c) FROM RULECHECK c WHERE (c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID"
		+ " AND c.report.task.id = ?1 AND c.report.task.deleted = false) AND TYPE(c) IN ?2"
	),
	@NamedQuery(name=Check.COUNT_FAILED_CHECKS_OF_TYPE, query="SELECT Count(c) from RULECHECK c WHERE"
		+ " c.outcome = de.unituebingen.validator.persistence.model.ValidationOutcome.NOT_VALID AND TYPE(c) IN ?1 AND c.report.task.deleted = false"
	)
})
@Entity(name="RULECHECK")
public class Check extends Persistable{
	// Query constants
	public static final String SELECT_FAILED_OF_TYPE_BY_ID_ASC = "Check.getFailedOfTypeByIdAscending";
	public static final String SELECT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID_BY_ID_ASC = "Check.getFailedOfTypeForReportIdByIdAscending";
	public static final String SELECT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID_BY_ID_ASC = "Check.getFailedOfTypeForTaskIdByIdAscending";
	public static final String COUNT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID = "Check.countFailedOfTypeForReportId";
	public static final String COUNT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID = "Check.countFailedOfTypeForTaskId";
	public static final String COUNT_FAILED_CHECKS_OF_TYPE = "Check.countFailedChecksOfType";

	/**
	 * The outcome result specified in rule
	 */
	@Enumerated(EnumType.STRING)
	private ValidationOutcome originalRuleOutcome;
	
	@Enumerated(EnumType.STRING)
	private ValidationOutcome outcome;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	private FileValidationReport report;
	
	@ElementCollection
	Map<String, String> messageTranslations = new HashMap<>();
		
	private String ruleName;
	
	
	public boolean isDeleted() {
		if(report != null && report.getTask() != null) {
			return report.getTask().isDeleted();
		}
		return false;
	}
	
	// Setters and Getters
	

	public ValidationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ValidationOutcome outcome) {
		this.outcome = outcome;
	}

	public FileValidationReport getReport() {
		return report;
	}

	public void setReport(FileValidationReport report) {
		this.report = report;
	}

	public ValidationOutcome getOriginalRuleOutcome() {
		return originalRuleOutcome;
	}

	public void setOriginalRuleOutcome(ValidationOutcome originalRuleOutcome) {
		this.originalRuleOutcome = originalRuleOutcome;
	}

	public Map<String, String> getMessageTranslations() {
		return messageTranslations;
	}

	public void setMessageTranslations(Map<String, String> messageTranslations) {
		this.messageTranslations = messageTranslations;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	

		
}
