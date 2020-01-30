package de.unituebingen.validator.beans;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import de.unituebingen.validator.common.qualifiers.Files;
import de.unituebingen.validator.common.qualifiers.Remove;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;
import de.unituebingen.validator.persistence.repository.BatchRepository;

/**
 * Clean up bean for deletion of batches
 *
 */
@Stateless
public class BatchCleanUpBean {

	@Inject
	BatchRepository batchRepository;

	@Inject
	SetupBean setup;

	@Inject
	Logger logger;

	@Inject
	@Remove
	@Files
	Event<String> deletePathEvent;

	@Inject
	TaskExecutorBean taskEjb;

	/**
	 * Remove old batches if allowed by configuration.
	 */
	public void removeOldBatches() {

		GlobalSettings globalSettings = setup.getGlobalSettings();
		if (!globalSettings.isAutoDeleteBatches())
			return;

		List<Batch> batches = batchRepository.findByDeletionState(false);
		for (Iterator<Batch> iterator = batches.iterator(); iterator.hasNext();) {
			Batch batch = (Batch) iterator.next();
			if (BatchCleanUpBean.getDateDiff(batch.getCreationDate(), new Date(), TimeUnit.HOURS) >= globalSettings
					.getAutoDeleteHours()) {
				logger.info("Removing old batch with id: " + String.valueOf(batch.getId()));
				batch.preRemove();
				batchRepository.remove(batch);
				deletePathEvent.fire(batch.getFolderPath());
			}
		}
	}

	/**
	 * Remove all batches marked for deletion.
	 */
	public void removeDeletedBatches() {
		List<Batch> batches = batchRepository.findByDeletionState(true);
		for (Iterator<Batch> iterator = batches.iterator(); iterator.hasNext();) {
			Batch batch = (Batch) iterator.next();
			if (batch.isDeleted()) {
				logger.info("Removing batch marked for deletion with id: " + String.valueOf(batch.getId()));
				batch.preRemove();
				batchRepository.remove(batch);
				deletePathEvent.fire(batch.getFolderPath());
			}
		}
	}

	/**
	 * Get a diff between two dates
	 * 
	 * @param date1
	 *            the oldest date
	 * @param date2
	 *            the newest date
	 * @param timeUnit
	 *            the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

}
