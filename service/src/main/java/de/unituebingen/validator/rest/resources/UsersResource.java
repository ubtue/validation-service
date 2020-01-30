package de.unituebingen.validator.rest.resources;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.net.URI;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.model.user.User;
import de.unituebingen.validator.persistence.repository.UserRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.authentication.PasswordUtils;
import de.unituebingen.validator.rest.authentication.SimpleKeyGenerator;
import de.unituebingen.validator.rest.converters.UserConverter;
import de.unituebingen.validator.rest.representations.UserRepresentation;
import de.unituebingen.validator.rest.representations.UsersRepresentation;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path(UsersResource.PATH)
@Transactional
public class UsersResource {

	public static final String PATH = "users";

	@Context
	private UriInfo uriInfo;

	@Inject
	private Logger logger;

	@Inject
	private SimpleKeyGenerator keyGenerator;

	@Inject
	UserRepository userRepository;

	@Inject
	SetupBean setup;

	@Resource(name = "jwtExpirationMinutes")
	private Integer jwtExpirationMinutes;

	@Resource(name = "jwtSignatureKey")
	private String jwtSignatureKey;

	@POST
	@Path("/login")
	@Consumes(APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("login") String login, @FormParam("password") String password) {
		try {
			// Authenticate the user using the credentials provided
			User user = authenticate(login, password);

			// Issue a token for the user
			String token = issueToken(user);

			// Return the token on the response
			return Response.ok().header("access-control-expose-headers", "Authorization")
					.header(AUTHORIZATION, "Bearer " + token).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception during authentication", e);
			return Response.status(UNAUTHORIZED).build();
		}
	}

	@JWTTokenNeeded(Permission = Role.ADMIN)
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(UserRepresentation rep) {
		User user = userRepository.findByLogin(rep.getUsername());

		if (user != null)
			throw new BadRequestException("A user with this name already exists");

		user = new User();
		user.setLogin(rep.getUsername());
		user.setPasswordDigest(PasswordUtils.digestPassword(rep.getPassword()));
		user.setRole(rep.getRole());
		userRepository.persist(user);
		URI userUri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getId())).build();
		return Response.created(userUri).entity(UserConverter.toRepresentation(user)).build();
	}

	@JWTTokenNeeded(Permission = Role.ADMIN)
	@Path("{id}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("id") long id) {
		User user = userRepository.findBy(id);

		if (user == null)
			throw new NotFoundException();

		return Response.ok(UserConverter.toRepresentation(user)).build();
	}

	@JWTTokenNeeded(Permission = Role.ADMIN)
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUsers(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("nameFilter") @DefaultValue("") String nameFilter) {

		int pageSize = setup.getGlobalSettings().getPageSize();
		String filterPattern = "%" + nameFilter + "%";

		List<User> users = userRepository.findByLoginLikeIgnoreCase(filterPattern, page * pageSize, pageSize);
		long totalUserCount = userRepository.countByLoginLikeIgnoreCase(filterPattern);

		// Create response
		UsersRepresentation usersRep = UserConverter.toCollectionRepresentation(totalUserCount, page, pageSize, users);
		usersRep.addQueryParameter("nameFilter", nameFilter);
		return Response.ok(usersRep).build();
	}

	@JWTTokenNeeded(Permission = Role.ADMIN)
	@Path("{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("id") long id) {
		User user = userRepository.findBy(id);

		if (user == null)
			throw new NotFoundException();

		userRepository.remove(user);

		return Response.ok().build();
	}

	@JWTTokenNeeded(Permission = Role.ADMIN)
	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") long id, UserRepresentation userRep) {
		User user = userRepository.findBy(id);

		if (user == null)
			throw new NotFoundException();

		if (id != userRep.getId().longValue())
			throw new BadRequestException("Update failed: id mismatch");

		// Check if user with login already exists
		if (!user.getLogin().equals(userRep.getUsername())) {
			User user2 = userRepository.findByLogin(userRep.getUsername());
			if (user2 != null)
				throw new BadRequestException("A user with this name already exists");
		}

		UserConverter.updateEntity(userRep, user);

		return Response.noContent().build();
	}

	private User authenticate(String login, String password) throws Exception {
		User user = userRepository.findByLogin(login);

		if (user == null || !PasswordUtils.digestPassword(password).equals(user.getPasswordDigest()))
			throw new SecurityException("Invalid user/password");

		return user;
	}

	private String issueToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", user.getLogin());
		claims.put("role", user.getRole().toString());
		Key key = keyGenerator.generateKey(jwtSignatureKey);
		String jwtToken = Jwts.builder().setClaims(claims).setIssuer(uriInfo.getAbsolutePath().toString())
				.setIssuedAt(new Date()).setExpiration(toDate(LocalDateTime.now().plusMinutes(jwtExpirationMinutes)))
				.signWith(SignatureAlgorithm.HS512, key).compact();

		return jwtToken;
	}

	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

}
