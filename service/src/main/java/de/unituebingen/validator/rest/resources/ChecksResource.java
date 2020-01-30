package de.unituebingen.validator.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.reports.checks.Check;
import de.unituebingen.validator.persistence.model.reports.checks.FileNameValidationCheck;
import de.unituebingen.validator.persistence.model.reports.checks.FitsRecordCheck;
import de.unituebingen.validator.persistence.model.reports.checks.VeraPdfPolicyCheck;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.CheckRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.CheckConverter;
import de.unituebingen.validator.rest.representations.reports.ChecksRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Path(ChecksResource.PATH)
public class ChecksResource {

	public static final String PATH = "checks";

	@Inject
	CheckRepository checkRepository;

	@Inject
	SetupBean setup;

	@Inject
	Logger logger;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCheck(@PathParam("id") long id, @QueryParam("locale") @DefaultValue("en") String locale) {
		Check check = checkRepository.findBy(id);

		if (check == null)
			throw new NotFoundException("Check not found");

		return Response
				.ok(CheckConverter.toRepresentation(check, setup.getGlobalSettings().getMessageTranslations(), locale))
				.build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getChecks(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("fileReportId") @DefaultValue("0") long fileReportId,
			@QueryParam("batchReportId") @DefaultValue("0") long batchReportId,
			@QueryParam("type") @DefaultValue("all") String checkType,
			@QueryParam("locale") @DefaultValue("en") String locale) {

		int pageSize = setup.getGlobalSettings().getPageSize();
		List<Object> types = createCheckTypesList(checkType);

		if ((batchReportId != 0) && (fileReportId != 0))
			throw new BadRequestException("Only one parameter allowed: fileReportId, batchReportId");

		List<Check> checks;
		long totalCheckCount;

		if (fileReportId != 0) {
			checks = checkRepository.findFailedOfTypesForFileReport(fileReportId, types, page * pageSize, pageSize);
			totalCheckCount = checkRepository.countFailedOfTypesForFileReport(types, fileReportId);
		} else if (batchReportId != 0) {
			checks = checkRepository.findFailedOfTypesForValidationTask(batchReportId, types, page * pageSize,
					pageSize);
			totalCheckCount = checkRepository.countFailedOfTypesForValidationTask(batchReportId, types);
		} else {
			checks = checkRepository.findFailedOfTypes(types, page * pageSize, pageSize);
			totalCheckCount = checkRepository.countFailedOfTypes(types);
		}

		// Generate representation
		ChecksRepresentation checksRep = CheckConverter.toCollectionRepresentation(totalCheckCount, pageSize, page,
				checks, setup.getGlobalSettings().getMessageTranslations(), locale);
		checksRep.addQueryParameter("type", checkType);

		if (fileReportId != 0) {
			checksRep.addQueryParameter("fileReportId", String.valueOf(fileReportId));
		}

		if (batchReportId != 0) {
			checksRep.addQueryParameter("batchReportId", String.valueOf(batchReportId));
		}

		return Response.ok(checksRep).build();
	}

	private List<Object> createCheckTypesList(String checkType) {
		List<Object> types = new ArrayList<>();
		switch (checkType) {
		case "all":
			types.add(FitsRecordCheck.class);
			types.add(FileNameValidationCheck.class);
			types.add(VeraPdfPolicyCheck.class);
			break;
		case "fits":
			types.add(FitsRecordCheck.class);
			break;
		case "name":
			types.add(FileNameValidationCheck.class);
			break;
		case "verapdf":
			types.add(VeraPdfPolicyCheck.class);
			break;
		default:
			throw new BadRequestException("Unrecognized check type: " + checkType);
		}
		return types;
	}

}
