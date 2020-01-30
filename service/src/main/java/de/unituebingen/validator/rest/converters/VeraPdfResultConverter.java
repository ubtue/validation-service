package de.unituebingen.validator.rest.converters;

import java.util.List;

import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertion;
import de.unituebingen.validator.persistence.model.reports.VeraPdfRecord;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.AssertionRepresentation;
import de.unituebingen.validator.rest.representations.reports.AssertionsRepresentation;
import de.unituebingen.validator.rest.representations.reports.VeraPdfResultRepresentation;

public class VeraPdfResultConverter {

	public static VeraPdfResultRepresentation toRepresentation(VeraPdfRecord record) {
		VeraPdfResultRepresentation representation = new VeraPdfResultRepresentation();
		representation.setId(record.getId());
		representation.setFileReportId(record.getReport().getId());
		representation.setCompliant(record.getCompliant());
		representation.setEncrypted(record.getEncrypted());
		representation.setFailedChecks(record.getFailedChecks());
		representation.setFailedPolicyChecks(record.getFailedPolicyChecks());
		representation.setFailedRules(record.getFailedRules());
		representation.setPassedChecks(record.getPassedChecks());
		representation.setPassedRules(record.getPassedRules());
		representation.setValidationProfile(record.getValidationProfile());
		representation.setExecutionError(record.hasExitedExceptionally());
		representation.setErrorMessage(record.getErrorMessage());
		return representation;
	}

	public static AssertionRepresentation toAssertionRepresentation(VeraPdfAssertion assertion) {
		AssertionRepresentation assertRep = new AssertionRepresentation();
		assertRep.setClause(assertion.getClause());
		assertRep.setContexts(assertion.getContexts());
		assertRep.setDescription(assertion.getDescription());
		assertRep.setFailedChecks(assertion.getFailedChecks());
		assertRep.setId(assertion.getId());
		assertRep.setObject(assertion.getObject());
		assertRep.setOccurences(assertion.getOccurences());
		assertRep.setPassedChecks(assertion.getPassedChecks());
		assertRep.setSpecification(assertion.getSpecification());
		assertRep.setStatus(assertion.getStatus());
		assertRep.setTest(assertion.getTest());
		assertRep.setTestNumber(assertion.getTestNumber());
		return assertRep;
	}

	public static AssertionsRepresentation toAssertionCollectionRepresentation(long totalCount, int pageSize, int page,
			List<AssertionRepresentation> assertions) {
		AssertionsRepresentation asrep = new AssertionsRepresentation(totalCount, assertions.size(), pageSize, page);
		asrep.getEmbedded().put(LinkRelations.VERAPDF_ASSERTIONS, assertions);
		return asrep;
	}

}
