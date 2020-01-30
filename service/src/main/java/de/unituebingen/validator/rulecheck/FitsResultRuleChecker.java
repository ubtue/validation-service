package de.unituebingen.validator.rulecheck;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationType;
import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;

public class FitsResultRuleChecker {

	public static FitsRecordCheck checkUploadWithRecordAndRule(FitsRecordValidationRule rule, FitsRecord record,
			FileUpload upload) {

		FitsRecordCheck check = new FitsRecordCheck();
		FitsRecordValidationType type = rule.getType();
		check.setValidationType(type);
		check.setMessageTranslations(rule.getMessageTranslations());
		check.setExtension(rule.getExtension());
		check.setMime(rule.getMime());
		check.setPuid(rule.getPuid());
		check.setOriginalRuleOutcome(rule.getOutcome());
		check.setToolName(rule.getToolName());
		check.setRuleName(rule.getName());

		// if tool names specified and diverging
		if (check.getToolName() != null && !record.getTool().equalsIgnoreCase(check.getToolName())) {
			check.setOutcome(ValidationOutcome.VALID);
			return check;
		}

		if (type == FitsRecordValidationType.MIME_TYPE_HAS_FILE_EXTENSION) {

			if (record.getMime() == null) {
				if (rule.getOutcomeOnMissingInformation() == ValidationOutcome.NOT_VALID) {
					check.setOutcome(ValidationOutcome.NOT_VALID);
				} else {
					check.setOutcome(ValidationOutcome.VALID);
				}
				return check;
			} else if (record.getMime().equals(rule.getMime())) {
				if (upload.getFileName().endsWith(rule.getExtension())) {
					check.setOutcome(rule.getOutcome());
				} else {
					check.setOutcome(getOppositeOutcome(rule.getOutcome()));
				}
			} else {
				check.setOutcome(ValidationOutcome.VALID);
			}

		} else if (type == FitsRecordValidationType.MIME_IS_VALID) {

			if (record.getMime() == null) {
				if (rule.getOutcomeOnMissingInformation() == ValidationOutcome.NOT_VALID) {
					check.setOutcome(ValidationOutcome.NOT_VALID);
				} else {
					check.setOutcome(ValidationOutcome.VALID);
				}
				return check;
			} else if (record.getMime().equals(rule.getMime())) {
				if (record.isValid() == null) {
					if (rule.getOutcomeOnMissingInformation() == ValidationOutcome.NOT_VALID) {
						check.setOutcome(ValidationOutcome.NOT_VALID);
					} else {
						check.setOutcome(ValidationOutcome.VALID);
					}
					return check;
				}

				if (record.isValid()) {
					check.setOutcome(rule.getOutcome());
				} else {
					check.setOutcome(getOppositeOutcome(rule.getOutcome()));
				}
			} else {
				check.setOutcome(ValidationOutcome.VALID);
				return check;
			}

		} else if (type == FitsRecordValidationType.PUID_IS_VALID) {

			if ((record.getPuid() == null) || (record.isValid() == null)) {
				if (rule.getOutcomeOnMissingInformation() == ValidationOutcome.NOT_VALID) {
					check.setOutcome(ValidationOutcome.NOT_VALID);
				} else {
					check.setOutcome(ValidationOutcome.VALID);
				}
				return check;
			} else if (record.getPuid().equals(rule.getPuid())) {
				if (record.isValid()) {
					check.setOutcome(rule.getOutcome());
				} else {
					check.setOutcome(getOppositeOutcome(rule.getOutcome()));
				}
			} else {
				check.setOutcome(ValidationOutcome.VALID);
			}
		}
		return check;
	}

	private static ValidationOutcome getOppositeOutcome(ValidationOutcome outcome) {
		if (outcome == ValidationOutcome.VALID) {
			return ValidationOutcome.NOT_VALID;
		} else {
			return ValidationOutcome.VALID;
		}
	}

}
