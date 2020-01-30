package de.unituebingen.validator.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.ValidationOutcome;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.repository.FileValidationReportRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.FileReportConverter;
import de.unituebingen.validator.rest.representations.reports.FileReportRepresentation;
import de.unituebingen.validator.rest.representations.reports.FileReportsRepresentation;

@JWTTokenNeeded(Permission = Role.USER)
@Transactional(rollbackOn=Exception.class)
@Path(FileReportResource.PATH)
public class FileReportResource {
	
	public static final String PATH = "file-reports";
	
	@Inject
	FileValidationReportRepository reportRepository;
	
	@Inject
	ValidationTaskRepository taskRepository;
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	Logger logger;
	
	@Inject
	SetupBean setup;
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFileReports(@QueryParam("page") @DefaultValue("0") Integer page, 
			@QueryParam("batchReportId") Long batchReportId,
			@QueryParam("nameFilter") @DefaultValue("")String nameFilter,
			@QueryParam("outcomeFilter") @DefaultValue("all")String outcomeFilter) {
		
		int pageSize = setup.getGlobalSettings().getPageSize();
		List<ValidationOutcome> validationOutcomeFilters = this.createValidationOutcomeFilterList(outcomeFilter);		
		String fileNamefilterPattern = "%"+ nameFilter +"%"; 		
		List<FileValidationReport> reports;
		long totalReportCount;
				
		if(batchReportId == null) 
			throw new BadRequestException("Query param is missing: batchReportId");
		
		if(nameFilter.length() == 0) {
			reports = reportRepository.findByTaskAndOutcomes(batchReportId, validationOutcomeFilters, page * pageSize, pageSize);
			totalReportCount = reportRepository.countByTaskAndOutcomes(batchReportId, validationOutcomeFilters);
		} else {
			reports = reportRepository.findByTaskAndOutcomesAndFileNameLikeIgnoreCase(batchReportId, validationOutcomeFilters, fileNamefilterPattern, page * pageSize, pageSize);
			totalReportCount = reportRepository.countByTaskAndOutcomesAndFileNameLikeIgnoreCase(batchReportId, fileNamefilterPattern,validationOutcomeFilters);
		}
				
		// Create representation
		FileReportsRepresentation filesRep = FileReportConverter.toCollectionRepresentation(totalReportCount, pageSize, page, reports, batchReportId);
		filesRep.addQueryParameter("outcomeFilter", outcomeFilter);
		return Response.ok(filesRep).build();
	}
	
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFileReport(@PathParam("id") long id) {
		FileValidationReport report = reportRepository.findBy(id);
		
		if(report == null || report.isDeleted())
			throw new NotFoundException("Report not found");
		
		long validationTaskId = taskRepository.findTaskIdForFileValidationReport(report);
		
		FileReportRepresentation representation;
		representation = FileReportConverter.toRepresentation(report, validationTaskId, report.getFileUpload());
		return Response.ok(representation).build();
	}
	
	
	private List<ValidationOutcome> createValidationOutcomeFilterList(String outcomeFilter) {
		List<ValidationOutcome> validationOutcomeFilters = new ArrayList<>();
		
		switch (outcomeFilter) {
		case "all":
			validationOutcomeFilters.add(ValidationOutcome.VALID);
			validationOutcomeFilters.add(ValidationOutcome.NOT_VALID);
			break;
		case "valid":
			validationOutcomeFilters.add(ValidationOutcome.VALID);
			break;
		case "notValid":
			validationOutcomeFilters.add(ValidationOutcome.NOT_VALID);
			break;
		default:
			throw new BadRequestException("Unknown outcome filter: " + outcomeFilter);
		}
		
		return validationOutcomeFilters;
	}

}
