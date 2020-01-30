package de.unituebingen.validator.persistence.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.Batch_;
import de.unituebingen.validator.persistence.model.user.User;
import de.unituebingen.validator.persistence.model.user.User_;

@Repository(forEntity = User.class)
public abstract class UserRepository extends AbstractFullEntityRepository<User, Long>{
	
	public User findByLogin(String login) {
		try {
			TypedQuery<User> query= entityManager().createNamedQuery(User.SELECT_BY_LOGIN, User.class);
			query.setParameter(1, login);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}	
	}
	
	public List<User> findByLoginLikeIgnoreCase(String filterPattern, int startPosition,  int maxResults) {
		return criteria()
				.likeIgnoreCase(User_.login, filterPattern)
				.createQuery()
				.setMaxResults(maxResults)
				.setFirstResult(startPosition)
				.getResultList();
	}
	
	@Query(named = User.COUNT_ALL_WITH_LOGIN_PATTERN)
	public abstract Long countByLoginLikeIgnoreCase(String pattern);

}
