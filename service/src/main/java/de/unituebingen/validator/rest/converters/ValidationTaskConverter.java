package de.unituebingen.validator.rest.converters;

import java.util.List;

import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.rest.representations.BatchRepresentation;
import de.unituebingen.validator.rest.representations.ConfigurationRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.QueueItemRepresentation;
import de.unituebingen.validator.rest.representations.QueueItemsRepresentation;

public class ValidationTaskConverter {

	/**
	 * Creates a {@link QueueItemRepresentation} from a {@link ValidationTask}.
	 * 
	 * @param task
	 *            The {@link ValidationTask}
	 * @return
	 */
	public static QueueItemRepresentation toRepresentation(ValidationTask task) {
		QueueItemRepresentation rep = new QueueItemRepresentation();
		rep.setStatus(task.getValidationStatus());
		rep.setId(task.getId());
		rep.setCreatedDate(task.getCreated().getTime());

		if (task.getBatch() != null) {
			rep.setBatchId(task.getBatch().getId());
			BatchRepresentation batchRep = BatchConverter.toRepresentation(task.getBatch());
			rep.getEmbedded().put(LinkRelations.BATCH, batchRep);
		}

		if (task.getProcessorConfiguration() != null) {
			rep.setConfigurationId(task.getProcessorConfiguration().getId());
			ConfigurationRepresentation confRep = ProcessorConfigurationConverter
					.toRepresentation(task.getProcessorConfiguration());
			rep.getEmbedded().put(LinkRelations.CONFIGURATION, confRep);
		}
		return rep;
	}

	public static QueueItemsRepresentation toCollectionRepresentation(long totalCount, int pageSize, int pageIndex,
			List<QueueItemRepresentation> items) {
		QueueItemsRepresentation queueItems = new QueueItemsRepresentation(totalCount, items.size(), pageSize,
				pageIndex);
		queueItems.getEmbedded().put(LinkRelations.QUEUE_ITEMS, items);
		return queueItems;
	}

}
