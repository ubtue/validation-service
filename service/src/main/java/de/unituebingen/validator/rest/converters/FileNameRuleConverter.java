package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.rules.FileNameRuleRepresentation;
import de.unituebingen.validator.rest.representations.rules.FileNameRulesRepresentation;

public class FileNameRuleConverter {

	public static FileNameRuleRepresentation toRepresentation(FileNameValidationRule rule) {
		FileNameRuleRepresentation representation = new FileNameRuleRepresentation();
		representation.setId(rule.getId());
		representation.setType(rule.getType());
		representation.setConfigurationId(rule.getConfiguration().getId());
		representation.setValue(rule.getComparisonValue());
		representation.setOutcome(rule.getOutcome());
		representation.setMessageTranslations(rule.getMessageTranslations());
		representation.setRuleName(rule.getName());
		return representation;
	}

	public static void updateEntity(FileNameValidationRule rule, FileNameRuleRepresentation rep) {
		rule.setComparisonValue(rep.getValue());
		rule.setMessageTranslations(rep.getMessageTranslations());
		rule.setName(rep.getRuleName());
		rule.setType(rep.getType());
		rule.setOutcome(rep.getOutcome());
	}

	public static FileNameRulesRepresentation toCollectionRepresentation(long totalCount, int page, int pageSize,
			List<FileNameValidationRule> rules, long configurationId) {
		FileNameRulesRepresentation rulesRep = new FileNameRulesRepresentation(totalCount, page, pageSize,
				rules.size());
		rulesRep.setConfigurationId(configurationId);

		List<FileNameRuleRepresentation> ruleRepList = new ArrayList<>();
		for (FileNameValidationRule rule : rules) {
			ruleRepList.add(FileNameRuleConverter.toRepresentation(rule));
		}
		rulesRep.getEmbedded().put(LinkRelations.FILE_NAME_RULES, ruleRepList);
		return rulesRep;
	}
}
