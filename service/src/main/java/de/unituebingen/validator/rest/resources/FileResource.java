package de.unituebingen.validator.rest.resources;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unituebingen.validator.common.qualifiers.Added;
import de.unituebingen.validator.common.qualifiers.Files;
import de.unituebingen.validator.common.qualifiers.Remove;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.FileUploadRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FileUploadConverter;
import de.unituebingen.validator.tasks.FileUploadTask;

@JWTTokenNeeded(Permission = Role.USER)
@Path(FileResource.PATH)
@Transactional(rollbackOn = Exception.class)
public class FileResource {

	public static final String PATH = "files";

	@Inject
	FileUploadRepository fileRepository;

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
	@Added
	@Task
	Event<Long> taskAddedEvent;

	@Inject
	Logger logger;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFile(@PathParam("id") long id) {
		FileUpload upload = fileRepository.findBy(id);

		if (upload == null || upload.isDeleted())
			throw new NotFoundException("File not found");

		return Response.ok(FileUploadConverter.toRepresentation(upload)).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteFile(@PathParam("id") long id) {
		FileUpload upload = fileRepository.findBy(id);

		if (upload == null || upload.isDeleted())
			throw new NotFoundException("File not found");

		upload.preRemove();
		fileRepository.remove(upload);
		return Response.noContent().build();
	}

}
