package de.unituebingen.validator.persistence.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.ValidationTask_;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;

public abstract class ValidationTaskRepository extends AbstractFullEntityRepository<ValidationTask, Long> {

	public List<ValidationTask> findProcessedForBatch(Batch batch, int startPosition, int maxResults) {
		return criteria()
				.notEq(ValidationTask_.validationStatus, ValidationStatus.PROCESSING)
				.notEq(ValidationTask_.validationStatus, ValidationStatus.QUEUED)
				.eq(ValidationTask_.deleted, false)
				.eq(ValidationTask_.batch, batch)
				.orderAsc(ValidationTask_.id)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<ValidationTask> findProcessed(int startPosition, int maxResults) {
		return criteria()
				.notEq(ValidationTask_.validationStatus, ValidationStatus.PROCESSING)
				.notEq(ValidationTask_.validationStatus, ValidationStatus.QUEUED)
				.eq(ValidationTask_.deleted, false)
				.orderDesc(ValidationTask_.processingFinished)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<ValidationTask> findProcessing() {
		return criteria()
				.eq(ValidationTask_.validationStatus, ValidationStatus.PROCESSING)
				.eq(ValidationTask_.deleted, false)
				.createQuery()
				.getResultList();
	}
	
	public List<ValidationTask> findQueuedOrdeByCreationAsc() {
		return criteria()
				.eq(ValidationTask_.validationStatus, ValidationStatus.QUEUED)
				.eq(ValidationTask_.deleted, false)
				.orderAsc(ValidationTask_.created)
				.createQuery()
				.getResultList();
	}
	
	
	public List<ValidationTask> findProcessingOrQueuedOrdeByCreationAsc(int startPosition, int maxResults) {
		return criteria()
				.notEq(ValidationTask_.validationStatus, ValidationStatus.FAILED)
				.notEq(ValidationTask_.validationStatus, ValidationStatus.FINISHED)
				.eq(ValidationTask_.deleted, false)
				.orderAsc(ValidationTask_.created)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<ValidationTask> findProcessingOrQueuedOrdeByCreationAsc() {
		return criteria()
				.notEq(ValidationTask_.validationStatus, ValidationStatus.FAILED)
				.notEq(ValidationTask_.validationStatus, ValidationStatus.FINISHED)
				.eq(ValidationTask_.deleted, false)
				.orderAsc(ValidationTask_.created)
				.createQuery()
				.getResultList();
	}
	
	
	public Long findTaskIdForFileValidationReport(FileValidationReport report) throws NoResultException, NonUniqueResultException {
		TypedQuery<Long> taskIdQuery = entityManager().createQuery("SELECT t.id FROM ValidationTask t WHERE ?1 MEMBER OF t.fileValidationReports", Long.class);
		taskIdQuery.setParameter(1, report);
		return taskIdQuery.getSingleResult();
	}
	
	
	@Query(named = ValidationTask.COUNT_ALL_FINISHED)
	public abstract Long countProcessed() ;

	@Query(named = ValidationTask.COUNT_ALL_FINISHED_FOR_BATCH)
	public abstract Long countProcessedForBatch(Batch batch) ;
	
	@Query(named = ValidationTask.COUNT_ALL_PROCESSING_AND_QUEUED)
	public abstract Long countProcessingAndQueued();
	
	@Query(named = ValidationTask.COUNT_ALL_PROCESSING)
	public abstract Long countProcessing();
	
	@Query(named = ValidationTask.COUNT_FILE_REPORTS_FOR_TASK_ID)
	public abstract Integer countFileReports(long taskId);
	
}
