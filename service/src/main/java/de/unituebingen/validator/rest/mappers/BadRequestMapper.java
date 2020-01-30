package de.unituebingen.validator.rest.mappers;

import javax.ws.rs.BadRequestException;
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
public class BadRequestMapper implements ExceptionMapper<BadRequestException> {

	@Context
	HttpHeaders headers;

	@Override
	public Response toResponse(BadRequestException exception) {
		ErrorMessage errMessage = new ErrorMessage();
		errMessage.setMessage(exception.getMessage());
		errMessage.setType(ErrorType.BAD_REQUEST.getDescription());
		MediaType type = MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType()) ? headers.getMediaType()
				: MediaType.APPLICATION_JSON_TYPE;
		return Response.status(Status.BAD_REQUEST).entity(errMessage).type(type).build();
	}

}
