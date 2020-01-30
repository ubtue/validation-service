package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;

import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;

public abstract class FileValidationReportRepository extends AbstractFullEntityRepository<FileValidationReport, Long> {
	
	public List<FileValidationReport> findByTaskAndOutcomes (long taskId, List<ValidationOutcome> outcomes, int startPosition,  int maxResults) {
		return entityManager().createNamedQuery(FileValidationReport.GET_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES, FileValidationReport.class)
				.setParameter(1, taskId)
				.setParameter(2, outcomes)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public List<FileValidationReport> findByTaskAndOutcomesAndFileNameLikeIgnoreCase (long taskId, List<ValidationOutcome> outcomes, String filterPattern ,int startPosition,  int maxResults) {
		return entityManager().createNamedQuery(FileValidationReport.GET_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES, FileValidationReport.class)
				.setParameter(1, taskId)
				.setParameter(2, filterPattern)
				.setParameter(3, outcomes)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	

	@Query(named = FileValidationReport.COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_VALIDATION_OUTCOMES)
	public abstract Long countByTaskAndOutcomes(long taskId, List<ValidationOutcome> outcomes);
	
	@Query(named = FileValidationReport.COUNT_REPORTS_FOR_TASK_ID_FILTER_BY_FILE_NAME_PATTERN_AND_VALIDATION_OUTCOMES)
	public abstract Long countByTaskAndOutcomesAndFileNameLikeIgnoreCase(long taskId, String filterPattern, List<ValidationOutcome> outcomes);
	
	

}
