package de.unituebingen.validator.rest.mappers;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.unituebingen.validator.persistence.model.ErrorMessage;
import de.unituebingen.validator.persistence.model.ErrorType;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

	@Context
	HttpHeaders headers;

	@Override
	public Response toResponse(NotFoundException exception) {
		ErrorMessage errMessage = new ErrorMessage();
		errMessage.setMessage(exception.getMessage());
		errMessage.setType(ErrorType.NOT_FOUND.getDescription());
		MediaType type = MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType()) ? headers.getMediaType()
				: MediaType.APPLICATION_JSON_TYPE;
		return Response.status(Status.NOT_FOUND).entity(errMessage).type(type).build();
	}

}
