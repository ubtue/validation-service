package de.unituebingen.validator.rest.mappers;

import javax.ws.rs.ServiceUnavailableException;
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
public class ServiceUnavailableMapper implements ExceptionMapper<ServiceUnavailableException> {

	@Context
	HttpHeaders headers;

	@Override
	public Response toResponse(ServiceUnavailableException exception) {
		ErrorMessage errMessage = new ErrorMessage();
		errMessage.setMessage(exception.getMessage());
		errMessage.setType(ErrorType.SERVICE_UNAVAILABLE.getDescription());
		MediaType type = MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType()) ? headers.getMediaType()
				: MediaType.APPLICATION_JSON_TYPE;
		return Response.status(Status.SERVICE_UNAVAILABLE).entity(errMessage).type(type).build();
	}

}
