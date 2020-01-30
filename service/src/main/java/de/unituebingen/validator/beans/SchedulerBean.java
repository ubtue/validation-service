package de.unituebingen.validator.beans;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

/**
 * Bean used for running tasks with a scheduled delay at fixed rate.
 *
 */
@Startup
@Singleton
public class SchedulerBean {

	private static final long INITIAL_DELAY_REMOVE_OLD = 0;
	private static final long PERIOD_REMOVE_OLD = 60;
	private static final long INITIAL_DELAY_REMOVE_DELETED = 5;
	private static final long PERIOD_REMOVE_DELETED = 15;

	@Resource
	ManagedScheduledExecutorService scheduler;

	@Inject
	BatchCleanUpBean cleanJob;

	@PostConstruct
	public void init() {
		this.scheduler.scheduleAtFixedRate(cleanJob::removeOldBatches, INITIAL_DELAY_REMOVE_OLD, PERIOD_REMOVE_OLD,
				TimeUnit.MINUTES);
		this.scheduler.scheduleAtFixedRate(cleanJob::removeDeletedBatches, INITIAL_DELAY_REMOVE_DELETED,
				PERIOD_REMOVE_DELETED, TimeUnit.MINUTES);
	}

}
