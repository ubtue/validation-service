package de.unituebingen.validator.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.unituebingen.validator.common.exceptions.ReportAssistantException;
import de.unituebingen.validator.common.qualifiers.Cancel;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.ErrorMessage;
import de.unituebingen.validator.persistence.model.ErrorType;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.tasks.BatchValidationTask;

/**
 * Bean for submitting and canceling validation tasks.
 *
 */
@Singleton
public class TaskExecutorBean {

	@Resource
	ManagedExecutorService executorService;
	@Inject
	Instance<BatchValidationTask> handlerInstance;
	@Inject
	SetupBean setup;
	@Inject
	ReportAssistantBean reportAssistant;
	@Inject
	Logger logger;

	/** Holds ValidationTask and associated future for every task in execution */
	private Map<Long, Future<?>> taskMap = new HashMap<>();

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void submitValidationTask(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Task Long id) {
		Long runningTasks = reportAssistant.getProcessingValidationTasksCount();
		Long tasksToExecute = setup.getGlobalSettings().getConcurrentTasks() - runningTasks;

		if (tasksToExecute == 0)
			return;

		List<ValidationTask> tasksList = reportAssistant.getQueuedValidationTasksByDateAsc();
		for (long i = 0; i < tasksToExecute; i++) {
			if (tasksList.size() > i) {
				ValidationTask task = tasksList.get((int) i);
				BatchValidationTask handler = handlerInstance.get();
				handler.setTaskId(task.getId());
				try {
					reportAssistant.updateValidationTask(task.getId(), ValidationStatus.PROCESSING, null);
					taskMap.put(task.getId(), executorService.submit(handler));
				} catch (RejectedExecutionException | ReportAssistantException e) {
					try {
						reportAssistant.updateValidationTask(task.getId(), ValidationStatus.FAILED,
								new ErrorMessage("Server error: validation task could not be scheduled for execution",
										ErrorType.SERVER_ERROR.getDescription()));
					} catch (ReportAssistantException e1) {
						logger.log(Level.SEVERE, "Failed to update validation task with id " + task.getId(), e1);
					}
					logger.log(Level.SEVERE,
							"ValidationTask with id " + task.getId() + " could not be scheduled for execution", e);
				}
			} else {
				return;
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void unscheduleTask(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Task @Cancel Long taskId) {
		cancelTaskWithId(taskId);
	}

	private boolean cancelTaskWithId(long id) {
		Future<?> task = this.taskMap.get(id);
		if ((task != null) && !task.isDone()) {
			return task.cancel(true);
		}
		return true;
	}

}
