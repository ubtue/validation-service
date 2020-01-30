package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;

import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertion;
import de.unituebingen.validator.persistence.model.reports.VeraPdfRecord;

public abstract class VeraPdfRecordRepository extends AbstractFullEntityRepository<VeraPdfRecord, Long> {

	public List<VeraPdfAssertion> findAssertionsForRecord(long recordId, int startPosition, int maxResults) {
		return entityManager().createNamedQuery(VeraPdfRecord.SELECT_ASSERTIONS_FOR_RECORD_WTIH_ID, VeraPdfAssertion.class)
				.setParameter(1, recordId)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	@Query(named = VeraPdfRecord.COUNT_ASSERTIONS_FOR_RECORD_WITH_ID)
	public abstract Integer countAssertionsForRecord(long recordId);
	
}
