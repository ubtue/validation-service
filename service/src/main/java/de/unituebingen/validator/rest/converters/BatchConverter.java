package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.rest.representations.BatchRepresentation;
import de.unituebingen.validator.rest.representations.BatchesRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.BatchReportRepresentation;

/**
 * Converter for {@link Batch}.
 *
 */
public class BatchConverter {

	/**
	 * Creates basic {@link BatchRepresentation} from a batch.
	 * 
	 * @param batch
	 *            The batch
	 * @return
	 */
	public static BatchRepresentation toRepresentation(Batch batch) {
		BatchRepresentation rep = new BatchRepresentation();
		rep.setCreationDate(batch.getCreationDate().getTime());
		rep.setDescription(batch.getName());
		rep.setId(batch.getId());
		return rep;
	}

	/**
	 * Creates basic {@link BatchRepresentation} from a batch.
	 * 
	 * @param batch
	 *            The batch.
	 * @param taskList
	 *            The task list with batch reports to embed.
	 * @return
	 */
	public static BatchRepresentation toRepresentation(Batch batch, List<ValidationTask> taskList) {
		BatchRepresentation rep = toRepresentation(batch);
		List<BatchReportRepresentation> repList = new ArrayList<>();
		for (ValidationTask task : taskList) {
			repList.add(BatchReportConverter.toRepresentation(task));
		}
		rep.getEmbedded().put(LinkRelations.BATCH_REPORTS, repList);
		return rep;
	}

	/**
	 * Creates a {@link BatchesRepresentation}.
	 * 
	 * @param totalCount
	 *            The amount of results in total.
	 * @param pageIndex
	 *            The index of the page.
	 * @param pageSize
	 *            The number of results per page in general.
	 * @param batchesList
	 *            The batches to be embedded in the representation page.
	 */
	public static BatchesRepresentation toCollectionRepresentation(long totalCount, int pageIndex, int pageSize,
			List<Batch> batchesList) {
		BatchesRepresentation brep = new BatchesRepresentation(totalCount, pageIndex, pageSize, batchesList.size());
		List<BatchRepresentation> batchReps = new ArrayList<>();

		for (Batch batch : batchesList) {
			BatchRepresentation batchRep = BatchConverter.toRepresentation(batch);
			batchReps.add(batchRep);
		}
		brep.getEmbedded().put(LinkRelations.BATCHES, batchReps);
		return brep;
	}

}
