package de.unituebingen.validator.rest.mappers;

import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.unituebingen.validator.persistence.model.ErrorMessage;
import de.unituebingen.validator.persistence.model.ErrorType;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

	@Inject
	private Logger logger;

	@Context
	HttpHeaders headers;

	@Override
	public Response toResponse(Exception exception) {
		logger.severe(exception.toString());
		ErrorMessage errMessage = new ErrorMessage();
		errMessage.setMessage("server error");
		errMessage.setType(ErrorType.SERVER_ERROR.getDescription());
		MediaType type = MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType()) ? headers.getMediaType()
				: MediaType.APPLICATION_JSON_TYPE;
		return Response.status(500).entity(errMessage).type(type).build();
	}
}