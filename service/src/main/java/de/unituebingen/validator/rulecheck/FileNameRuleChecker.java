package de.unituebingen.validator.rulecheck;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;
import de.unituebingen.validator.persistence.model.reports.checks.FileNameValidationCheck;

public class FileNameRuleChecker {

	public static FileNameValidationCheck checkByValidatingUploadWithRule(FileNameValidationRule rule,
			FileUpload upload, Map<String, Pattern> regExPatterns) {

		FileNameValidationCheck check = new FileNameValidationCheck();
		check.setMessageTranslations(rule.getMessageTranslations());
		check.setComparisonValue(rule.getComparisonValue());
		FileNameValidationType type = rule.getType();
		check.setValidationType(type);
		check.setOriginalRuleOutcome(rule.getOutcome());
		check.setRuleName(rule.getName());

		if (type == FileNameValidationType.REGEX_MATCHES || type == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
			Pattern pattern = regExPatterns.get(rule.getComparisonValue());
			Matcher matcher = pattern.matcher(upload.getFileName());

			if (matcher.matches()) {
				if (type == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
					check.setOutcome(getInvertedOutcome(rule.getOutcome()));
				} else {
					check.setOutcome(rule.getOutcome());
				}
			} else {
				if (type == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
					check.setOutcome(rule.getOutcome());
				} else {
					check.setOutcome(getInvertedOutcome(rule.getOutcome()));
				}
			}
		}
		return check;
	}

	private static ValidationOutcome getInvertedOutcome(ValidationOutcome outcome) {
		if (outcome == ValidationOutcome.VALID) {
			return ValidationOutcome.NOT_VALID;
		} else {
			return ValidationOutcome.VALID;
		}
	}

}
