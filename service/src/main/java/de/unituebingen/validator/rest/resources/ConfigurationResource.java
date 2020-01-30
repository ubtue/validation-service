package de.unituebingen.validator.rest.resources;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfExecutionMode;
import de.unituebingen.validator.persistence.model.configuration.VeraPdfPolicyOptions;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationRule;
import de.unituebingen.validator.persistence.model.configuration.rules.FileNameValidationType;
import de.unituebingen.validator.persistence.model.configuration.rules.FitsRecordValidationRule;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.ProcessorConfigurationRepository;
import de.unituebingen.validator.persistence.repository.RuleRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FileNameRuleConverter;
import de.unituebingen.validator.rest.converters.FitsResultRuleConverter;
import de.unituebingen.validator.rest.converters.ProcessorConfigurationConverter;
import de.unituebingen.validator.rest.converters.VeraPdfConfigurationConverter;
import de.unituebingen.validator.rest.representations.ConfigurationRepresentation;
import de.unituebingen.validator.rest.representations.ConfigurationsRepresentation;
import de.unituebingen.validator.rest.representations.VeraPdfConfigurationRepresentation;
import de.unituebingen.validator.rest.representations.rules.FileNameRuleRepresentation;
import de.unituebingen.validator.rest.representations.rules.FileNameRulesRepresentation;
import de.unituebingen.validator.rest.representations.rules.FitsResultRuleRepresentation;

@JWTTokenNeeded(Permission = Role.ADMIN)
@Path(ConfigurationResource.PATH)
@Transactional(rollbackOn = { Exception.class })
public class ConfigurationResource {

	public static final String PATH = "configurations";
	public static final String PATH_VERAPDF_SETUP = "verapdf-setup";

	@Inject
	ProcessorConfigurationRepository configRepository;

	@Inject
	RuleRepository ruleRepository;

	@Context
	private UriInfo uriInfo;

	@Inject
	Logger logger;

	@Inject
	SetupBean setup;

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createConfiguration() {
		ProcessorConfiguration config = new ProcessorConfiguration();
		config.setFitsActive(true);
		config.setCreationDate(new Date());
		config.setDescription("New Configuration");

		VeraPdfConfiguration veraConf = new VeraPdfConfiguration();
		veraConf.setFailedChecksPerRuleDisplayed(1);
		veraConf.setFailedChecksThreshold(1);
		veraConf.setReportPassedRules(false);
		veraConf.setExecutionMode(VeraPdfExecutionMode.FILE_EXTENSION);
		veraConf.setPdfPolicies(new VeraPdfPolicyOptions());

		config.setVeraPdfConfiguration(veraConf);
		configRepository.persist(config);

		URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(config.getId())).build();
		return Response.created(uri).entity(ProcessorConfigurationConverter.toRepresentation(config)).build();
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateConfiguration(@PathParam("id") long id, ConfigurationRepresentation configRep) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Update failed: configuration not found");

		if (!procCon.getId().equals(configRep.getId()))
			throw new BadRequestException("Update failed: configuration id mismatch");

		// Check for non unique public identifier and create error message, if so.
		List<ProcessorConfiguration> configList = configRepository
				.findByPublicIdentifier(configRep.getPublicIdentifier());

		if (configList.size() == 1) {
			if (!configList.get(0).getId().equals(configRep.getId())) {
				throw new BadRequestException("Update failed: non unique public identifier");
			}
		}

		VeraPdfConfigurationRepresentation veraConfRep = VeraPdfConfigurationConverter
				.deserializeEmbeddedVeraPdfConfiguration(configRep);

		if (veraConfRep != null) {
			if (veraConfRep.getId().longValue() != procCon.getVeraPdfConfiguration().getId().longValue())
				throw new BadRequestException("Update failed: verapdf configuration id mismatch.");
		}
		ProcessorConfigurationConverter.updateEntity(procCon, configRep);
		VeraPdfConfigurationConverter.updateEntity(procCon.getVeraPdfConfiguration(), veraConfRep);
		return Response.noContent().build();
	}

	@JWTTokenNeeded(Permission = Role.USER)
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConfiguration(@PathParam("id") long id) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Configuration not found");

		return Response.ok(ProcessorConfigurationConverter.toRepresentation(procCon)).build();
	}

	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteConfiguration(@PathParam("id") long id) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Configuration not found");
		procCon.preRemove();
		configRepository.remove(procCon);
		return Response.noContent().build();
	}

	@JWTTokenNeeded(Permission = Role.USER)
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConfigurations(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("descriptionFilter") @DefaultValue("") String descriptionFilter) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<ProcessorConfiguration> configs;
		long totalCount;

		if (descriptionFilter.length() != 0) {
			String filterPattern = "%" + descriptionFilter + "%";
			configs = configRepository.findByDescriptionLikeIgnoreCase(filterPattern, page * pageSize, pageSize);
			totalCount = configRepository.countByDescriptionLikeIgnoreCase(filterPattern);
		} else {
			configs = configRepository.findAllByCreationDateDesc(page * pageSize, pageSize);
			totalCount = configRepository.count();
		}

		// Create response
		ConfigurationsRepresentation configsRep = ProcessorConfigurationConverter.toCollectionRepresentation(totalCount,
				page, pageSize, configs);
		configsRep.addQueryParameter("descriptionFilter", descriptionFilter);
		return Response.ok(configsRep).build();
	}

	@GET
	@Path("{id}/" + PATH_VERAPDF_SETUP)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getVeraPdfConfiguration(@PathParam("id") long id) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Configuration not found");
		return Response.ok(VeraPdfConfigurationConverter.toRepresentation(procCon.getVeraPdfConfiguration())).build();
	}

	@PUT
	@Path("{id}/" + PATH_VERAPDF_SETUP)
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response updateVeraPdfConfiguration(@PathParam("id") long id, VeraPdfConfigurationRepresentation rep) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Configuration not found");

		if (!rep.getId().equals(procCon.getVeraPdfConfiguration().getId()))
			throw new BadRequestException("Update failed: verapdf configuration id mismatch");

		VeraPdfConfigurationConverter.updateEntity(procCon.getVeraPdfConfiguration(), rep);

		return Response.noContent().build();
	}

	@GET
	@Path("{id}/" + FileNameRuleResource.PATH)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFileNameRules(@PathParam("id") long id, @QueryParam("page") @DefaultValue("0") Integer page) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<FileNameValidationRule> rules = ruleRepository.findByTypeForConfigurationId(FileNameValidationRule.class,
				id, page * pageSize, pageSize);
		long totalCount = ruleRepository.countByTypeForConfigurationId(FileNameValidationRule.class, id);

		// Create representation
		FileNameRulesRepresentation rulesRep = FileNameRuleConverter.toCollectionRepresentation(totalCount, page,
				pageSize, rules, id);
		return Response.ok(rulesRep).build();
	}

	@POST
	@Path("{id}/" + FileNameRuleResource.PATH)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createFileNameRule(@PathParam("id") long id, FileNameRuleRepresentation rep) {
		ProcessorConfiguration procCon = configRepository.findBy(id);
		if (procCon == null)
			throw new NotFoundException("Configuration not found");
		FileNameValidationRule rule = new FileNameValidationRule();
		FileNameRuleConverter.updateEntity(rule, rep);

		if (rule.getType() == FileNameValidationType.REGEX_MATCHES
				|| rule.getType() == FileNameValidationType.REGEX_DOES_NOT_MATCH) {
			try {
				@SuppressWarnings("unused")
				Pattern pat = Pattern.compile(rule.getComparisonValue());
			} catch (PatternSyntaxException e) {
				throw new BadRequestException("Regular expression pattern compilation failed with exception");
			}
		}

		ruleRepository.persist(rule);
		procCon.addRule(rule);
		return Response.ok(FileNameRuleConverter.toRepresentation(rule)).build();
	}

	@POST
	@Path("{id}/" + FitsResultRuleResource.PATH)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createFitsResultRule(@PathParam("id") long id, FitsResultRuleRepresentation rep) {
		ProcessorConfiguration procCon = configRepository.findBy(id);

		if (procCon == null)
			throw new NotFoundException("Configuration not found");

		FitsRecordValidationRule rule = new FitsRecordValidationRule();
		FitsResultRuleConverter.updateEntity(rule, rep);
		ruleRepository.persist(rule);
		procCon.addRule(rule);
		return Response.ok(FitsResultRuleConverter.toRepresentation(rule)).build();
	}

	@GET
	@Path("{id}/" + FitsResultRuleResource.PATH)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFitsResultRules(@PathParam("id") long id, @QueryParam("page") @DefaultValue("0") Integer page) {
		int pageSize = setup.getGlobalSettings().getPageSize();

		List<FitsRecordValidationRule> rules = ruleRepository
				.findByTypeForConfigurationId(FitsRecordValidationRule.class, id, page * pageSize, pageSize);
		long totalCount = ruleRepository.countByTypeForConfigurationId(FitsRecordValidationRule.class, id);

		return Response.ok(FitsResultRuleConverter.toCollectionRepresentation(totalCount, page, pageSize, rules, id))
				.build();
	}

}
