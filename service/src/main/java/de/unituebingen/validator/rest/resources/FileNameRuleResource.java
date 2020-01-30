package de.unituebingen.validator.rest.resources;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.RuleRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FileNameRuleConverter;
import de.unituebingen.validator.rest.representations.rules.FileNameRuleRepresentation;

@JWTTokenNeeded(Permission = Role.ADMIN)
@Path(FileNameRuleResource.PATH)
public class FileNameRuleResource {

	public static final String PATH = "file-name-rules";

	@Inject
	SetupBean setup;

	@Inject
	Logger logger;

	@Inject
	RuleRepository ruleRepository;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRule(@PathParam("id") long id) {
		FileNameValidationRule rule = ruleRepository.findByIdAndType(id, FileNameValidationRule.class);

		if (rule == null)
			throw new NotFoundException("Rule not found");

		return Response.ok(FileNameRuleConverter.toRepresentation(rule)).build();
	}

	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public Response deleteRule(@PathParam("id") long id) {
		FileNameValidationRule rule = ruleRepository.findByIdAndType(id, FileNameValidationRule.class);

		if (rule == null)
			throw new NotFoundException("Rule not found");

		ruleRepository.remove(rule);
		return Response.noContent().build();
	}

	@PUT
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Transactional
	public Response updateRule(@PathParam("id") long id, FileNameRuleRepresentation rep) {
		FileNameValidationRule rule = ruleRepository.findByIdAndType(rep.getId(), FileNameValidationRule.class);

		if (rule == null)
			throw new NotFoundException("Rule not found");

		if (id != rep.getId().longValue())
			throw new BadRequestException("Update failed: id mismatch");

		FileNameRuleConverter.updateEntity(rule, rep);

		if (rule.getType() == FileNameValidationType.REGEX_MATCHES
				|| rule.getType() == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
			try {
				Pattern pat = Pattern.compile(rule.getComparisonValue());
			} catch (PatternSyntaxException e) {
				throw new BadRequestException("Regular expression pattern compilation failed with exception");
			}
		}
		return Response.noContent().build();
	}

}
