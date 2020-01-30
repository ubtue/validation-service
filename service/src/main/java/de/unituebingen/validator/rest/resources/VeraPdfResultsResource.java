package de.unituebingen.validator.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.NoResultException;
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
import de.unituebingen.validator.persistence.model.reports.VeraPdfAssertion;
import de.unituebingen.validator.persistence.model.reports.VeraPdfRecord;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.VeraPdfRecordRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.VeraPdfResultConverter;
import de.unituebingen.validator.rest.representations.reports.AssertionRepresentation;
import de.unituebingen.validator.rest.representations.reports.AssertionsRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Path(VeraPdfResultsResource.PATH)
public class VeraPdfResultsResource {

	public static final String PATH = "verapdf-results";

	@Inject
	SetupBean setup;

	@Inject
	Logger logger;

	@Inject
	VeraPdfRecordRepository veraRepository;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getReport(@PathParam("id") long id) {
		VeraPdfRecord record = veraRepository.findBy(id);

		if (record == null || record.isDeleted())
			throw new NotFoundException("VeraPDF report not found");

		return Response.ok(VeraPdfResultConverter.toRepresentation(record)).build();
	}

	@GET
	@Path("{id}/assertions")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getReportAssertions(@PathParam("id") long id, @QueryParam("page") @DefaultValue("0") Integer page) {
		int pageSize = setup.getGlobalSettings().getPageSize();
		VeraPdfRecord record = veraRepository.findBy(id);

		if (record == null || record.isDeleted())
			throw new NotFoundException("VeraPDF report not found");

		List<VeraPdfAssertion> assertions = veraRepository.findAssertionsForRecord(id, page * pageSize, pageSize);
		List<AssertionRepresentation> assertionRepList = new ArrayList<>();
		for (VeraPdfAssertion veraPdfAssertion : assertions) {
			if (veraPdfAssertion != null)
				assertionRepList.add(VeraPdfResultConverter.toAssertionRepresentation(veraPdfAssertion));
		}

		long totalCount;

		try {
			totalCount = veraRepository.countAssertionsForRecord(id);
		} catch (NoResultException e) {
			totalCount = 0;
		}

		AssertionsRepresentation ar = VeraPdfResultConverter.toAssertionCollectionRepresentation(totalCount, pageSize,
				page, assertionRepList);
		ar.setVeraResultId(id);
		return Response.ok(ar).build();
	}

}
