package de.unituebingen.validator.rest.config;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
/**
 * Cors filter for the rest service.
 * 
 *
 */
public class CorsResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		if (headers.get("Access-Control-Allow-Origin") == null)
			headers.add("Access-Control-Allow-Origin", "*");
		if (headers.get("Access-Control-Allow-Methods") == null)
			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
		if (headers.get("Access-Control-Allow-Headers") == null)
			headers.add("Access-Control-Allow-Headers", "Authorization, origin, content-type, accept ");
	}

}
