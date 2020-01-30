package de.unituebingen.validator.persistence.repository;

import java.util.List;
import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import de.unituebingen.validator.persistence.model.reports.checks.Check;

public abstract class CheckRepository extends AbstractFullEntityRepository<Check, Long>{
	
	@Query(named = Check.COUNT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID)
	public abstract Long countFailedOfTypesForFileReport(List<Object> types, long reportId);
	
	@Query(named = Check.COUNT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID)
	public abstract Long countFailedOfTypesForValidationTask(long taskId, List<Object> types);
	
	@Query(named = Check.COUNT_FAILED_CHECKS_OF_TYPE)
	public abstract Long countFailedOfTypes(List<Object> types);

	public List<Check> findFailedOfTypesForFileReport(long id, List<Object> types, int startPosition, int maxResults) {
		return entityManager().createNamedQuery(Check.SELECT_FAILED_OF_TYPE_FOR_FILE_REPORT_ID_BY_ID_ASC, Check.class)
				.setParameter(1, id)
				.setParameter(2, types)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
		
	public List<Check> findFailedOfTypesForValidationTask(long taskId, List<Object> types, int startPosition, int maxResults) {
		return entityManager().createNamedQuery(Check.SELECT_FAILED_OF_TYPE_FOR_VALIDATION_TASK_ID_BY_ID_ASC, Check.class)
				.setParameter(1, taskId)
				.setParameter(2, types)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<Check> findFailedOfTypes(List<Object> types, int startPosition, int maxResults) {
		return entityManager().createNamedQuery(Check.SELECT_FAILED_OF_TYPE_BY_ID_ASC, Check.class)
				.setParameter(1, types)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}

}
