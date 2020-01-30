package de.unituebingen.validator.persistence.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;

import de.unituebingen.validator.persistence.model.configuration.rules.Rule;

public abstract class RuleRepository  extends AbstractFullEntityRepository<Rule, Long> {

	public <T> List<T> findByTypeForConfigurationId(Class<T> typeKey, long configurationId, int startPosition, int maxResults) {
		return (List<T>) entityManager().createNamedQuery(Rule.SELECT_ALL_WITH_TYPE_FOR_CONFIG_ID_ORDER_BY_LAST_MODIFIED_DESC, typeKey)
				.setParameter(1, typeKey)
				.setParameter(2, configurationId)
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	public <T> List<T> findByTypeForConfigurationId(Class<T> typeKey, long configurationId) {
		return (List<T>) entityManager().createNamedQuery(Rule.SELECT_ALL_WITH_TYPE_FOR_CONFIG_ID_ORDER_BY_LAST_MODIFIED_DESC, typeKey)
				.setParameter(1, typeKey)
				.setParameter(2, configurationId)
				.getResultList();
	}
	
	@Query(named = Rule.COUNT_ALL_WITH_TYPE_FOR_CONFIG_ID)
	public abstract <T> Long countByTypeForConfigurationId(Class<T> type, long configurationId);
	
	
	public <T> T findByIdAndType(long id, Class<T> type) {
		try {
			return (T) entityManager().createNamedQuery(Rule.SELECT_RULES_BY_ID_AND_TYPE, type)
					.setParameter(1, id)
					.setParameter(2, type)
					.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

}
