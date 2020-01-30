package de.unituebingen.validator.rest.resources;

import java.util.List;
import javax.inject.Inject;
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
import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.FitsRecordRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FitsResultConverter;
import de.unituebingen.validator.rest.representations.reports.FitsResultsRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Path(FitsResultsResource.PATH)
public class FitsResultsResource {

	public static final String PATH = "fits-results";

	@Inject
	FitsRecordRepository fitsRepository;

	@Inject
	SetupBean setup;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getResults(@QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("fileReportId") Long fileReportId) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<FitsRecord> records;
		long totalBatchCount;

		if (fileReportId != null) {
			records = fitsRepository.findBy(fileReportId, page * pageSize, pageSize);
			totalBatchCount = fitsRepository.countByFileValidationReport(fileReportId);
		} else {
			records = fitsRepository.findByDateDesc(page * pageSize, pageSize);
			totalBatchCount = fitsRepository.countIgnoreDeleted();
		}

		FitsResultsRepresentation resultsRep = FitsResultConverter.toCollectionRepresentation(totalBatchCount, page,
				pageSize, records);

		if (fileReportId != null)
			resultsRep.addQueryParameter("fileReportId", String.valueOf(fileReportId));

		return Response.ok(resultsRep).build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getResult(@PathParam("id") @DefaultValue("0") long id) {
		FitsRecord record = fitsRepository.findBy(id);

		if (record == null || record.isDeleted())
			throw new NotFoundException("Fits result with id " + String.valueOf(id) + " not found");

		return Response.ok(FitsResultConverter.toRepresentation(record)).build();

	}

}
