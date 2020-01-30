package de.unituebingen.validator.rest.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.beans.TaskExecutorBean;
import de.unituebingen.validator.common.exceptions.ReportAssistantException;
import de.unituebingen.validator.common.qualifiers.Cancel;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.BatchRepository;
import de.unituebingen.validator.persistence.repository.ProcessorConfigurationRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.ValidationTaskConverter;
import de.unituebingen.validator.rest.representations.QueueItemRepresentation;
import de.unituebingen.validator.rest.representations.QueueItemsRepresentation;
import de.unituebingen.validator.rest.representations.reports.BatchReportOrderRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Path(QueueResource.PATH)
@Transactional(rollbackOn = Exception.class)
public class QueueResource {

	public static final String PATH = "queue";

	@Inject
	ValidationTaskRepository taskRepository;

	@Inject
	BatchRepository batchRepository;

	@Inject
	ProcessorConfigurationRepository configRepository;

	@Context
	private UriInfo uriInfo;

	@Resource(name = "baseDirectory")
	private String baseDirectory;

	@Inject
	Logger logger;

	@Inject
	SetupBean setup;

	@Inject
	@Task
	Event<Long> taskAddedEvent;

	@Inject
	@Task
	@Cancel
	Event<Long> unscheduleTaskEvent;

	@Inject
	TaskExecutorBean taskEjb;

	/**
	 * Get all validation tasks that are either queued up or processing.
	 * 
	 * @param page
	 *            the page index to be returned.
	 * @return List of QueueItems.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional(value = TxType.SUPPORTS)
	public Response getQueueItems(@QueryParam("page") @DefaultValue("0") Integer page) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<ValidationTask> tasks = taskRepository.findProcessingOrQueuedOrdeByCreationAsc(page * pageSize, pageSize);
		long taskCount = taskRepository.countProcessingAndQueued();

		List<QueueItemRepresentation> itemsList = new ArrayList<>();
		for (ValidationTask task : tasks) {
			QueueItemRepresentation itRep = ValidationTaskConverter.toRepresentation(task);
			if (task.getValidationStatus() == ValidationStatus.PROCESSING) {
				int reportsCount;
				int uploadsCount = 0;

				try {
					uploadsCount = batchRepository.countFileUploadss(task.getBatch().getId());
					reportsCount = taskRepository.countFileReports(task.getId());
				} catch (NoResultException e) {
					reportsCount = 0;
				}
				long progress = (uploadsCount != 0 ? Math.floorDiv(reportsCount * 100, uploadsCount) : 0);
				itRep.setProgress(progress);
			}
			itemsList.add(itRep);
		}
		QueueItemsRepresentation itemsRep = ValidationTaskConverter.toCollectionRepresentation(taskCount, pageSize,
				page, itemsList);
		return Response.ok(itemsRep).build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional(value = TxType.SUPPORTS)
	public Response getQueueItem(@PathParam("id") Long id) {
		ValidationTask task = taskRepository.findBy(id);

		if (task == null || task.isDeleted())
			throw new NotFoundException("Id not found in queue");

		if ((task.getValidationStatus() == ValidationStatus.FINISHED)
				|| (task.getValidationStatus() == ValidationStatus.FAILED)) {
			URI uri = uriInfo.getBaseUriBuilder().path(BatchReportResource.class).path(String.valueOf(task.getId()))
					.build();
			return Response.seeOther(uri).build();
		}

		QueueItemRepresentation item = ValidationTaskConverter.toRepresentation(task);

		if (task.getValidationStatus() == ValidationStatus.PROCESSING) {
			int reportsCount;
			int uploadsCount = 0;
			try {
				uploadsCount = batchRepository.countFileUploadss(task.getBatch().getId());
				reportsCount = taskRepository.countFileReports(task.getId());

			} catch (NoResultException e) {
				reportsCount = 0;
			}
			long progress = (uploadsCount != 0 ? Math.floorDiv(reportsCount * 100, uploadsCount) : 0);
			item.setProgress(progress);
		}
		return Response.ok(item).build();

	}

	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteFromQueue(@PathParam("id") Long id) throws ReportAssistantException {
		ValidationTask task = taskRepository.findBy(id);

		if (task == null || task.isDeleted())
			throw new NotFoundException("Id not found in queue");

		if (task.getValidationStatus() == ValidationStatus.PROCESSING
				|| task.getValidationStatus() == ValidationStatus.QUEUED) {
			unscheduleTaskEvent.fire(id);
			task.setValidationStatus(ValidationStatus.FAILED);
			task.setDeleted(true);
			task.setProcessingFinished(new Date());
		}
		return Response.ok().build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createReport(BatchReportOrderRepresentation batchReportOrder) {

		if (batchReportOrder == null)
			throw new BadRequestException("No report order included");

		Batch batch = batchRepository.findBy(batchReportOrder.getBatchId());

		if (batch == null || batch.isDeleted())
			throw new NotFoundException("Batch not found");

		List<ProcessorConfiguration> configList = configRepository
				.findByPublicIdentifier(batchReportOrder.getConfigurationIdentifier());

		if (configList.size() != 1)
			throw new NotFoundException("Configuration with public identifier "
					+ String.valueOf(batchReportOrder.getConfigurationIdentifier()) + " not found");

		ProcessorConfiguration procCon = configList.get(0);

		ValidationTask task = new ValidationTask();
		procCon.addValidationTask(task);
		task.setValidationStatus(ValidationStatus.QUEUED);
		batch.addValidationTask(task);
		task.setCreated(new Date());
		taskRepository.persist(task);
		taskAddedEvent.fire(task.getId());

		URI uri = uriInfo.getBaseUriBuilder().path(QueueResource.class).path(String.valueOf(task.getId())).build();
		return Response.accepted().location(uri).build();
	}

}
