package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.Batch_;

@Repository(forEntity = Batch.class)
public abstract class BatchRepository extends AbstractFullEntityRepository<Batch, Long>{
	
	public List<Batch> findByNameLikeIgnoreCaseDateDesc(String filterPattern, int startPosition,  int maxResults) {
		return criteria()
				.likeIgnoreCase(Batch_.name, filterPattern)
				.eq(Batch_.deleted, false)
				.orderDesc(Batch_.creationDate)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<Batch> findByNameLikeIgnoreCaseDateAsc(String filterPattern, int startPosition,  int maxResults) {
		return criteria()
				.likeIgnoreCase(Batch_.name, filterPattern)
				.eq(Batch_.deleted, false)
				.orderAsc(Batch_.creationDate)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	/**
	 * Selects all batches that have the same deleted status.
	 * @param deleted The status of the batches to be queried.
	 * @return
	 */
	public List<Batch> findByDeletionState(boolean deleted) {
		return criteria()
				.eq(Batch_.deleted, deleted)
				.createQuery()
				.getResultList();
	}
	
	@Query(named = Batch.COUNT_ALL_WITH_NAME_PATTERN)
	public abstract Long countByNameLikeIgnoreCase(String pattern);
	
	@Query(named = Batch.COUNT_FILE_UPLOADS_FOR_BATCH_ID)
	public abstract Integer countFileUploadss(long batchId);


}


