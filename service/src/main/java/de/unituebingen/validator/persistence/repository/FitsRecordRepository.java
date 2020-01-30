package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;

import de.unituebingen.validator.persistence.model.reports.FitsRecord;

public abstract class FitsRecordRepository extends AbstractFullEntityRepository<FitsRecord, Long> {

	public List<FitsRecord> findBy (long fileReportId, int startPosition,  int maxResults) {
		return entityManager().createNamedQuery(FitsRecord.GET_BY_FILE_REPORT_ID, FitsRecord.class)
				.setParameter(1, fileReportId)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<FitsRecord> findByDateDesc(int startPosition,  int maxResults) {
		return entityManager().createNamedQuery(FitsRecord.GET_BY_DATE_DESC, FitsRecord.class)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	@Query(named = FitsRecord.COUNT_BY_FILE_REPORT_ID)
	public abstract Long countByFileValidationReport(long reportId);
	
	@Query(named = FitsRecord.COUNT)
	public abstract Long countIgnoreDeleted();

}
