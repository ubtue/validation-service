package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.BatchReportRepresentation;
import de.unituebingen.validator.rest.representations.reports.BatchReportsRepresentation;

public class BatchReportConverter {

	public static BatchReportRepresentation toRepresentation(ValidationTask task) {
		BatchReportRepresentation brep = new BatchReportRepresentation();
		brep.setBatchId(task.getBatch().getId());
		if (task.getProcessorConfiguration() != null) {
			brep.setConfigurationId(task.getProcessorConfiguration().getId());
			brep.getEmbedded().put(LinkRelations.CONFIGURATION,
					ProcessorConfigurationConverter.toRepresentation(task.getProcessorConfiguration()));
		}

		brep.setCreationDate(task.getCreated().getTime());
		brep.setFinishedDate(task.getProcessingFinished().getTime());
		brep.setId(task.getId());
		brep.setStatus(task.getValidationStatus());
		brep.getEmbedded().put(LinkRelations.BATCH, BatchConverter.toRepresentation(task.getBatch()));

		if (task.getResultSummary() != null)
			brep.setSummary(BatchResultSummaryConverter.toRepresentation(task.getResultSummary()));

		return brep;
	}

	public static BatchReportsRepresentation toCollectionRepresentation(long totalCount, int pageSize, int page,
			List<ValidationTask> tasks) {
		BatchReportsRepresentation brep = new BatchReportsRepresentation(totalCount, pageSize, page, tasks.size());
		List<BatchReportRepresentation> reportRepresentations = new ArrayList<>();
		for (ValidationTask task : tasks) {
			reportRepresentations.add(BatchReportConverter.toRepresentation(task));
		}
		brep.getEmbedded().put(LinkRelations.BATCH_REPORTS, reportRepresentations);
		return brep;
	}
}
