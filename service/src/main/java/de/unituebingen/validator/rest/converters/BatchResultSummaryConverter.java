package de.unituebingen.validator.rest.converters;

import de.unituebingen.validator.persistence.model.TaskResultSummary;
import de.unituebingen.validator.rest.representations.ValidationOutcome;
import de.unituebingen.validator.rest.representations.reports.BatchResultSummaryRepresentation;

public class BatchResultSummaryConverter {

	public static BatchResultSummaryRepresentation toRepresentation(TaskResultSummary summary) {
		BatchResultSummaryRepresentation br = new BatchResultSummaryRepresentation();
		br.setProblematicFiles(summary.getFilesWithProblems());
		br.setTotalFiles(summary.getFilesTotal());
		br.setValidationOutcome(
				summary.getValidationOutcome() == de.unituebingen.validator.persistence.model.ValidationOutcome.VALID
						? ValidationOutcome.VALID
						: ValidationOutcome.NOT_VALID);

		return br;
	}

}
