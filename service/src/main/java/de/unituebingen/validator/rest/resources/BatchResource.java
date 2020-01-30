package de.unituebingen.validator.rest.resources;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
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
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.common.Sequencer;
import de.unituebingen.validator.common.qualifiers.Cancel;
import de.unituebingen.validator.common.qualifiers.Files;
import de.unituebingen.validator.common.qualifiers.Remove;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.BatchRepository;
import de.unituebingen.validator.persistence.repository.FileUploadRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.BatchConverter;
import de.unituebingen.validator.rest.converters.FileUploadConverter;
import de.unituebingen.validator.rest.representations.BatchRepresentation;
import de.unituebingen.validator.rest.representations.BatchesRepresentation;
import de.unituebingen.validator.rest.representations.FilesRepresentation;
import de.unituebingen.validator.rest.resources.enums.BatchesSortOrder;
import de.unituebingen.validator.tasks.FileUploadTask;

@JWTTokenNeeded(Permission = Role.USER)
@Path(BatchResource.PATH)
@Transactional(rollbackOn = Exception.class)
public class BatchResource {

	public static final String PATH = "batches";

	@Inject
	BatchRepository batchRepository;

	@Inject
	ValidationTaskRepository taskRepository;

	@Inject
	FileUploadRepository uploadRepository;

	@Context
	private UriInfo uriInfo;

	@Resource(name = "baseDirectory")
	private String baseDirectory;

	@Inject
	FileUploadTask fileUploadHandler;

	@Inject
	@Remove
	@Files
	Event<String> deletePathEvent;

	@Inject
	SetupBean setup;

	@Inject
	@Task
	Event<Long> taskAddedEvent;

	@Inject
	Logger logger;

	@Inject
	@Task
	@Cancel
	Event<Long> unscheduleTaskEvent;

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createBatch(BatchRepresentation batchRepresentation) {
		Batch batch = new Batch();
		batch.setCreationDate(new Date());

		if (batchRepresentation != null && batchRepresentation.getDescription() != null) {
			batch.setName(batchRepresentation.getDescription());
		} else {
			batch.setName("Undefined");
		}

		String dirPath = baseDirectory + File.separator + String.valueOf(Sequencer.getSequence()) + "_"
				+ java.util.UUID.randomUUID().toString();

		if (!new File(dirPath).mkdirs())
			throw new ServerErrorException("Batch allocation failed", 500);

		batch.setFolderPath(dirPath);
		batchRepository.save(batch);
		BatchRepresentation batchRep = BatchConverter.toRepresentation(batch);
		URI batchUri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(batch.getId())).build();

		return Response.created(batchUri).entity(batchRep).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBatches(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("orderBy") @DefaultValue("DATE_DESC") BatchesSortOrder sortOrder,
			@QueryParam("descriptionFilter") @DefaultValue("") String descriptionFilter) {

		int pageSize = setup.getGlobalSettings().getPageSize();
		String filterPattern = "%" + descriptionFilter.toUpperCase() + "%";

		List<Batch> batches;

		if (sortOrder == BatchesSortOrder.DATE_ASC) {
			batches = batchRepository.findByNameLikeIgnoreCaseDateAsc(filterPattern, page * pageSize, pageSize);
		} else {
			batches = batchRepository.findByNameLikeIgnoreCaseDateDesc(filterPattern, page * pageSize, pageSize);
		}

		long totalBatchCount = batchRepository.countByNameLikeIgnoreCase(filterPattern);

		// Create response
		BatchesRepresentation batchesRep = BatchConverter.toCollectionRepresentation(totalBatchCount, page, pageSize,
				batches);
		batchesRep.addQueryParameter("sortOrder", sortOrder.toString());
		batchesRep.addQueryParameter("descriptionFilter", descriptionFilter);
		return Response.ok(batchesRep).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteBatch(@PathParam("id") long id) {
		Batch batch = batchRepository.findBy(id);

		if (batch == null || batch.isDeleted())
			throw new NotFoundException("Batch not found");

		batch.setDeleted(true);
		for (ValidationTask task : batch.getValidationTasks()) {
			task.setDeleted(true);
			if (task.getValidationStatus() != ValidationStatus.FINISHED) {
				task.setValidationStatus(ValidationStatus.FAILED);
				unscheduleTaskEvent.fire(task.getId());
			}
		}
		return Response.noContent().build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBatch(@PathParam("id") long id) {
		Batch batch = batchRepository.findBy(id);

		if (batch == null || batch.isDeleted())
			throw new NotFoundException("Batch not found");

		List<ValidationTask> tasks = taskRepository.findProcessedForBatch(batch, 0,
				setup.getGlobalSettings().getEmbeddedResources());
		
		BatchRepresentation batchRep = BatchConverter.toRepresentation(batch, tasks);
		return Response.ok(batchRep).build();
	}

	@GET
	@Path("{id}/" + FileResource.PATH)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFiles(@PathParam("id") long id, @QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("fileNameFilter") @DefaultValue("") String fileNameFilter) {

		int pageSize = setup.getGlobalSettings().getPageSize();
		Batch batch = batchRepository.findBy(id);

		if (batch == null || batch.isDeleted())
			throw new NotFoundException("Batch not found");

		String filterPattern = "%" + fileNameFilter.toUpperCase() + "%";

		List<FileUpload> fileUploads = uploadRepository.findByBatchAndFileNameLikeIgnoreCase(batch, filterPattern,
				page * pageSize, pageSize);
		long totalFilesCount = uploadRepository.countByBatchAndNameLikeIgnoreCase(batch, filterPattern);

		// Create representation
		FilesRepresentation filesRep = FileUploadConverter.toCollectionRepresentation(totalFilesCount, page, pageSize,
				fileUploads);
		
		filesRep.setBatchId(batch.getId());
		filesRep.addQueryParameter("fileNameFilter", fileNameFilter);
		return Response.ok(filesRep).build();
	}

	@POST
	@Path("{id}/" + FileResource.PATH)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public void uploadFiles(final FormDataMultiPart multiPart, @PathParam("id") long id,
			@Suspended final AsyncResponse asyncResponse) {
		List<FormDataBodyPart> bodyParts = multiPart.getFields("file");
		fileUploadHandler.setBatchId(id);
		fileUploadHandler.setFiles(bodyParts);
		fileUploadHandler.setResponse(asyncResponse);
		fileUploadHandler.setBatchUpload(true);
		URI uri = uriInfo.getBaseUriBuilder().path(BatchResource.class).path(String.valueOf(id)).path("files").build();
		fileUploadHandler.setBaseLink(uri.toString());
		new Thread(fileUploadHandler).start();
	}

}
