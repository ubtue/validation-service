package de.unituebingen.validator.persistence.repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;

import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;

public abstract class GlobalSettingsRepository extends AbstractFullEntityRepository<GlobalSettings, Long> {
	
	public GlobalSettings find() throws NoResultException, NonUniqueResultException {
		return entityManager().createNamedQuery(GlobalSettings.GET_GLOBAL_SETTINGS, GlobalSettings.class)
				.getSingleResult();
	}


}
