package de.unituebingen.validator.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.BatchRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.BatchReportConverter;
import de.unituebingen.validator.rest.representations.reports.BatchReportsRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Transactional(rollbackOn = Exception.class)
@Path(BatchReportResource.PATH)
public class BatchReportResource {

	public static final String PATH = "batch-reports";

	@Inject
	ValidationTaskRepository taskRepository;

	@Inject
	BatchRepository batchRepository;

	@Context
	private UriInfo uriInfo;

	@Inject
	SetupBean setup;

	@Inject
	Logger logger;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBatchReports(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("batchId") @DefaultValue("0") Long batchId) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<ValidationTask> tasks = new ArrayList<>();
		long totalTasksCount = 0;

		if (batchId == 0) {
			tasks = taskRepository.findProcessed(page * pageSize, pageSize);
			totalTasksCount = taskRepository.countProcessed();
		} else {
			Batch batch = batchRepository.findBy(batchId);
			if (batch != null) {
				tasks = taskRepository.findProcessedForBatch(batch, page * pageSize, pageSize);
				totalTasksCount = taskRepository.countProcessedForBatch(batch);
			}
		}

		// Create representation
		BatchReportsRepresentation batchReportsRep = BatchReportConverter.toCollectionRepresentation(totalTasksCount,
				pageSize, page, tasks);
		if (batchId != 0) {
			batchReportsRep.addQueryParameter("batchId", String.valueOf(batchId));
		}

		return Response.ok(batchReportsRep).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public Response getBatchReport(@PathParam("id") long id) {
		ValidationTask task = taskRepository.findBy(id);

		if (task == null || task.isDeleted())
			throw new NotFoundException("Report id not found");

		return Response.ok(BatchReportConverter.toRepresentation(task)).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteBatchReport(@PathParam("id") long id) {
		ValidationTask task = taskRepository.findBy(id);

		if (task == null || task.isDeleted())
			throw new NotFoundException("Report id not found");

		task.setDeleted(true);

		return Response.noContent().build();
	}

}
