package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.FileUpload_;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;

@Repository(forEntity = FileUpload.class)
public abstract class FileUploadRepository extends AbstractFullEntityRepository<FileUpload, Long> {
	
	
	public List<FileUpload> findByBatchAndFileNameLikeIgnoreCase(Batch batch, String namePattern, int startPosition, int maxResults) {		
		return criteria()
				.eq(FileUpload_.batch, batch)
				.likeIgnoreCase(FileUpload_.fileName, namePattern)
				.orderDesc(FileUpload_.creationDate)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	@Query(named = FileUpload.SELECT_BY_BATCH_STARTING_AFTER_UPLOAD_ID_ORDER_BY_UPLOAD_ID_ASC_)
	public List<FileUpload> findByBatchStartingAfterIdAsc(Batch batch, long startAfterId, int maxResults) {	
		return entityManager().createNamedQuery(FileUpload.SELECT_BY_BATCH_STARTING_AFTER_UPLOAD_ID_ORDER_BY_UPLOAD_ID_ASC_, FileUpload.class)
				.setParameter(1, batch)
				.setParameter(2, startAfterId)
				.setMaxResults(maxResults)
				.getResultList();
	}
	
	@Query(named = FileUpload.COUNT_BY_BATCH_AND_NAME_LIKE_PATTERN)
	public abstract Long countByBatchAndNameLikeIgnoreCase(Batch batch, String namePattern);

}
