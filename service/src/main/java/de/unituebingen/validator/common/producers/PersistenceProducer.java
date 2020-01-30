package de.unituebingen.validator.common.producers;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersistenceProducer {

	@Produces
	@Dependent
	@PersistenceContext(unitName = "dspacevalidator-psu")
	public EntityManager entityManager;

}
