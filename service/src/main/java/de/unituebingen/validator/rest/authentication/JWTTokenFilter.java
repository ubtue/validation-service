package de.unituebingen.validator.rest.authentication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.unituebingen.validator.persistence.model.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenFilter implements ContainerRequestFilter {

	@Inject
	private SimpleKeyGenerator keyGenerator;

	@Context
	private ResourceInfo resourceInfo;

	@Inject
	Logger logger;

	@Resource(name = "jwtSignatureKey")
	private String jwtSignatureKey;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			// Get jwt context annotation from resource method or (alternatively) resource
			// class
			Method method = resourceInfo.getResourceMethod();
			Class<?> resourceClass = resourceInfo.getResourceClass();

			JWTTokenNeeded resourceJWTAnnotation = null;

			if (method != null && method.isAnnotationPresent(JWTTokenNeeded.class)) {
				resourceJWTAnnotation = method.getAnnotation(JWTTokenNeeded.class);
			} else if (resourceClass.isAnnotationPresent(JWTTokenNeeded.class)) {
				resourceJWTAnnotation = resourceClass.getAnnotation(JWTTokenNeeded.class);
			}

			if (resourceJWTAnnotation == null || resourceJWTAnnotation.Permission().equals(Role.NO_RIGHTS))
				return;

			// Get the HTTP Authorization header from the request
			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

			// Extract the token from the HTTP Authorization header
			String token = authorizationHeader != null ? authorizationHeader.substring("Bearer".length()).trim() : null;

			// Validate the token
			Key key = keyGenerator.generateKey(jwtSignatureKey);
			Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);

			// Get role from resource annotation
			Role permission = resourceJWTAnnotation.Permission();

			// Get role claim from received jwt
			String roleClaim = claims.getBody().get("role", String.class);
			Role userRole = Role.valueOf(roleClaim);

			if (!userRole.equals(Role.ADMIN) && !permission.equals(userRole)) {
				throw new Exception("no roles");
			}

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

}
