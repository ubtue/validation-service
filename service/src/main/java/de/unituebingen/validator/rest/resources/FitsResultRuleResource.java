package de.unituebingen.validator.rest.resources;

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
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.RuleRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FitsResultRuleConverter;
import de.unituebingen.validator.rest.representations.rules.FitsResultRuleRepresentation;

@JWTTokenNeeded(Permission = Role.ADMIN)
@Path(FitsResultRuleResource.PATH)
public class FitsResultRuleResource {

	public static final String PATH = "fits-result-rules";

	@Inject
	SetupBean setup;

	@Inject
	RuleRepository ruleRepository;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRule(@PathParam("id") long id) {
		FitsRecordValidationRule rule = ruleRepository.findByIdAndType(id, FitsRecordValidationRule.class);

		if (rule == null)
			throw new NotFoundException("Rule not found");

		return Response.ok(FitsResultRuleConverter.toRepresentation(rule)).build();
	}

	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public Response deleteRule(@PathParam("id") long id) {
		FitsRecordValidationRule rule = ruleRepository.findByIdAndType(id, FitsRecordValidationRule.class);

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
	public Response updateRule(@PathParam("id") long id, FitsResultRuleRepresentation rep) {
		FitsRecordValidationRule rule = ruleRepository.findByIdAndType(rep.getId(), FitsRecordValidationRule.class);

		if (rule == null)
			throw new NotFoundException("Rule not found");

		if (id != rep.getId().longValue())
			throw new BadRequestException("Update failed: id mismatch");

		FitsResultRuleConverter.updateEntity(rule, rep);
		return Response.noContent().build();
	}

}
