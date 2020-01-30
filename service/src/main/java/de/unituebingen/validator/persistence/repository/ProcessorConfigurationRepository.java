package de.unituebingen.validator.persistence.repository;

import java.util.List;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration_;

public abstract class ProcessorConfigurationRepository extends AbstractFullEntityRepository<ProcessorConfiguration, Long> {
	
	@Query(named = ProcessorConfiguration.SELECT_BY_PUBLIC_IDENTIFIER)
	public abstract List<ProcessorConfiguration> findByPublicIdentifier(String identifier);
	
	public  List<ProcessorConfiguration> findByDescriptionLikeIgnoreCase(String descriptionPattern,  int startPosition,  int maxResults) {
		return criteria()
				.likeIgnoreCase(ProcessorConfiguration_.description, descriptionPattern)
				.orderDesc(ProcessorConfiguration_.creationDate)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	
	public  List<ProcessorConfiguration> findAllByCreationDateDesc(int startPosition,  int maxResults) {
		return entityManager().createNamedQuery(ProcessorConfiguration.SELECT_ALL_BY_CREATION_DATE_DESC, ProcessorConfiguration.class)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	
	
	@Query(named = ProcessorConfiguration.COUNT_ALL_WITH_DESCRIPTION_PATTERN)
	public abstract Long countByDescriptionLikeIgnoreCase(String descriptionPattern);

}
