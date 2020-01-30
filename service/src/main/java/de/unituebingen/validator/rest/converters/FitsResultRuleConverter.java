package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.rules.FitsResultRuleRepresentation;
import de.unituebingen.validator.rest.representations.rules.FitsResultRulesRepresentation;

public class FitsResultRuleConverter {

	public static FitsResultRuleRepresentation toRepresentation(FitsRecordValidationRule rule) {
		FitsResultRuleRepresentation representation = new FitsResultRuleRepresentation();
		representation.setConfigurationId(rule.getConfiguration().getId());
		representation.setExtension(rule.getExtension());
		representation.setMessageTranslations(rule.getMessageTranslations());
		representation.setId(rule.getId());
		representation.setMime(rule.getMime());
		representation.setOutcome(rule.getOutcome());
		representation.setPuid(rule.getPuid());
		representation.setType(rule.getType());
		representation.setOutcomeOnMissingFitsRecord(rule.getOutcomeOnMissingInformation());
		representation.setToolName(rule.getToolName());
		representation.setRuleName(rule.getName());
		return representation;
	}

	public static void updateEntity(FitsRecordValidationRule rule, FitsResultRuleRepresentation representation) {
		rule.setMessageTranslations(representation.getMessageTranslations());
		rule.setExtension(representation.getExtension());
		rule.setMime(representation.getMime());
		rule.setPuid(representation.getPuid());
		rule.setName(representation.getRuleName());
		rule.setOutcome(representation.getOutcome());
		rule.setOutcomeOnMissingInformation(representation.getOutcomeOnMissingFitsRecord());
		rule.setToolName(representation.getToolName());
		rule.setType(representation.getType());
	}

	public static FitsResultRulesRepresentation toCollectionRepresentation(long totalCount, int page, int pageSize,
			List<FitsRecordValidationRule> rules, long configurationId) {
		FitsResultRulesRepresentation rulesRep = new FitsResultRulesRepresentation(totalCount, page, pageSize,
				rules.size(), configurationId);
		rulesRep.setConfigurationId(configurationId);

		List<FitsResultRuleRepresentation> ruleRepList = new ArrayList<>();
		for (FitsRecordValidationRule rule : rules) {
			ruleRepList.add(FitsResultRuleConverter.toRepresentation(rule));
		}

		rulesRep.getEmbedded().put(LinkRelations.FITS_RESULT_RULES, ruleRepList);
		return rulesRep;
	}

}
