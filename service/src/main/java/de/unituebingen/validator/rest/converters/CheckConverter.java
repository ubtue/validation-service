package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.unituebingen.validator.persistence.model.configuration.MessageTranslations;
import de.unituebingen.validator.persistence.model.reports.checks.Check;
import de.unituebingen.validator.persistence.model.reports.checks.FileNameValidationCheck;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;
import de.unituebingen.validator.persistence.model.reports.checks.VeraPdfPolicyCheck;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.CheckRepresentation;
import de.unituebingen.validator.rest.representations.reports.CheckType;
import de.unituebingen.validator.rest.representations.reports.ChecksRepresentation;
import de.unituebingen.validator.verapdf.PdfValidationPolicies;

public class CheckConverter {

	public static ChecksRepresentation toCollectionRepresentation(long totalCount, int pageSize, int page,
			List<Check> checks, MessageTranslations translations, String locale) {
		ChecksRepresentation crep = new ChecksRepresentation(totalCount, pageSize, page, checks.size(), locale);
		crep.addQueryParameter("locale", locale);

		List<CheckRepresentation> checkReps = new ArrayList<>();
		for (Check check : checks) {
			checkReps.add(CheckConverter.toRepresentation(check, translations, locale));
		}
		crep.getEmbedded().put(LinkRelations.CHECKS, checkReps);
		return crep;
	}

	public static <T extends Check> CheckRepresentation toRepresentation(T check, MessageTranslations translations,
			String locale) {

		if (check instanceof FileNameValidationCheck) {
			return CheckConverter.fromFileNameValidationCheck((FileNameValidationCheck) check, locale);
		} else if (check instanceof FitsRecordCheck) {
			return CheckConverter.fromFitsRecordCheck((FitsRecordCheck) check, locale);
		} else if (check instanceof VeraPdfPolicyCheck) {
			return CheckConverter.fromVeraPdfPolicyCheck((VeraPdfPolicyCheck) check, translations, locale);
		}

		return null;
	}

	private static CheckRepresentation fromVeraPdfPolicyCheck(VeraPdfPolicyCheck veraCheck,
			MessageTranslations translations, String locale) {
		CheckRepresentation checkRep = new CheckRepresentation();
		checkRep.setId(veraCheck.getId());
		checkRep.setCheckType(CheckType.VERAPDF_POLICY);
		checkRep.setOutcome(veraCheck.getOutcome());
		checkRep.setResultMessage(getLocalizedPolicyResultMessage(veraCheck, locale, translations));
		if (veraCheck.getDetails() != null) {
			checkRep.setResultMessage(checkRep.getResultMessage() + veraCheck.getDetails());
		}

		if (veraCheck.getTest() != null && veraCheck.getLocation() != null) {
			checkRep.setTest(veraCheck.getTest() + " at location: " + veraCheck.getLocation());
		} else {
			checkRep.setTest(veraCheck.getRuleName());
		}

		checkRep.setReportId(veraCheck.getReport().getId());
		return checkRep;
	}

	private static CheckRepresentation fromFileNameValidationCheck(FileNameValidationCheck fileNameCheck,
			String locale) {
		CheckRepresentation checkRep = new CheckRepresentation();
		checkRep.setId(fileNameCheck.getId());
		checkRep.setCheckType(CheckType.FILENAME);
		checkRep.setOutcome(fileNameCheck.getOutcome());
		checkRep.setResultMessage(getLocalizedResultMessage(fileNameCheck, locale));
		checkRep.setTest("if(" + CheckType.FILENAME.getType() + " " + fileNameCheck.getValidationType().getValue()
				+ ": '" + fileNameCheck.getComparisonValue() + "') then outcome is: "
				+ fileNameCheck.getOriginalRuleOutcome().getOutcome());
		checkRep.setReportId(fileNameCheck.getReport().getId());
		return checkRep;
	}

	private static CheckRepresentation fromFitsRecordCheck(FitsRecordCheck fitsCheck, String locale) {
		CheckRepresentation checkRep = new CheckRepresentation();
		checkRep.setId(fitsCheck.getId());
		checkRep.setCheckType(CheckType.FITS_RESULT);
		checkRep.setOutcome(fitsCheck.getOutcome());
		checkRep.setResultMessage(getLocalizedResultMessage(fitsCheck, locale));
		String test = fitsCheck.getValidationType().getValue();
		test += (fitsCheck.getExtension() != null ? " , extension: " + fitsCheck.getExtension() : "");
		test += (fitsCheck.getMime() != null ? " , mime: " + fitsCheck.getMime() : "");
		test += (fitsCheck.getPuid() != null ? " , puid: " + fitsCheck.getMime() : "");
		checkRep.setTest(test);
		checkRep.setReportId(fitsCheck.getReport().getId());
		return checkRep;

	}

	private static String getLocalizedResultMessage(Check check, String locale) {
		String message = check.getMessageTranslations().get(locale);
		if (message != null) {
			return message;
		} else if (check.getMessageTranslations() == null || check.getMessageTranslations().size() == 0) {
			return "No message descriptions found for this rule";
		} else {
			// Load english as default if locale is not supported
			return check.getMessageTranslations().get("en");
		}
	}

	private static String getLocalizedPolicyResultMessage(VeraPdfPolicyCheck check, String locale,
			MessageTranslations translations) {

		switch (check.getPolicyKey()) {
		case PdfValidationPolicies.MESSAGE_KEY_ATTACHMENTS_POLICY:
			return getLocalizedMessageFromMap(translations.getAttachmentsTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_ENCRYPT_POLICY:
			return getLocalizedMessageFromMap(translations.getEncryptTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_FILES_POLICY:
			return getLocalizedMessageFromMap(translations.getFilesTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_FONTS_POLICY:
			return getLocalizedMessageFromMap(translations.getFontsTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_MULTIMDIA_POLICY:
			return getLocalizedMessageFromMap(translations.getMultimediaTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_PDFA_VALID_ENCRYPTED_POLICY:
			return getLocalizedMessageFromMap(translations.getPdfAEncryptedTranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_PDFA_VALID_POLICY:
			return getLocalizedMessageFromMap(translations.getPdfATranslations(), locale);
		case PdfValidationPolicies.MESSAGE_KEY_TRAILER_ENCRYPT_POLICY:
			return getLocalizedMessageFromMap(translations.getTrailerEncryptTranslations(), locale);
		default:
			break;
		}

		return "";
	}

	private static String getLocalizedMessageFromMap(Map<String, String> map, String locale) {
		String message = map.get(locale);

		if (message != null)
			return message;

		message = map.get("en");

		if (message != null)
			return message;

		return "No message descriptions found for this rule";
	}

}
