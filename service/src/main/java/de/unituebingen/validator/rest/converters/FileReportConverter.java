package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import de.unituebingen.validator.rest.representations.FileRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.FileReportRepresentation;
import de.unituebingen.validator.rest.representations.reports.FileReportsRepresentation;
import de.unituebingen.validator.rest.representations.reports.FitsResultRepresentation;

public class FileReportConverter {

	public static FileReportsRepresentation toCollectionRepresentation(long totalCount, int pageSize, int page,
			List<FileValidationReport> reports, long batchReportId) {
		FileReportsRepresentation frep = new FileReportsRepresentation(totalCount, pageSize, page, reports.size(),
				batchReportId);
		frep.addQueryParameter("batchReportId", String.valueOf(batchReportId));

		List<FileReportRepresentation> fileReports = new ArrayList<>();
		for (FileValidationReport report : reports) {
			fileReports.add(FileReportConverter.toRepresentation(report, batchReportId, report.getFileUpload()));
		}
		frep.getEmbedded().put(LinkRelations.FILE_REPORTS, fileReports);
		return frep;
	}

	public static final FileReportRepresentation toRepresentation(FileValidationReport report, long validationTaskId,
			FileUpload upload) {
		FileReportRepresentation fileRep = new FileReportRepresentation();
		fileRep.setBatchReportId(validationTaskId);
		fileRep.setFileId(report.getFileUpload() == null ? 0 : report.getFileUpload().getId());
		fileRep.setId(report.getId());

		fileRep.setFailedFitsChecks(report.getFailedFitsChecks());
		fileRep.setFailedNameChecks(report.getFailedNameChecks());

		if (report.getVeraPdfRecord() != null) {
			fileRep.setVeraPdfReportId(report.getVeraPdfRecord().getId());
			fileRep.getEmbedded().put(LinkRelations.VERAPDF_RESULT,
					VeraPdfResultConverter.toRepresentation(report.getVeraPdfRecord()));
		}

		fileRep.setValidationOutcome(report.getValidationOutcome());

		if (upload != null) {
			FileRepresentation file = new FileRepresentation();
			file.setBatchId(upload.getBatch().getId());
			file.setFileName(upload.getFileName());
			file.setId(upload.getId());
			file.setPath(upload.getFilePath());
			fileRep.getEmbedded().put(LinkRelations.FILE_UPLOAD, file);
		}

		FileReportConverter.embedFitsRecords(fileRep, report.getFitsRecords());
		fileRep.setFitsExecutionOutcome(report.getFitsExecutionOutcome());
		fileRep.setVeraPdfExecutionOutcome(report.getVeraPdfExecutionOutcome());
		fileRep.setErrorMessages(report.getErrorMessages());

		return fileRep;
	}

	private static void embedFitsRecords(FileReportRepresentation frep, List<FitsRecord> records) {
		if (records == null)
			frep.getEmbedded().put(LinkRelations.FITS_RESULTS, new ArrayList<>());

		List<FitsResultRepresentation> fitsRepList = new ArrayList<>();
		records.forEach(record -> fitsRepList.add(FitsResultConverter.toRepresentation(record)));
		frep.getEmbedded().put(LinkRelations.FITS_RESULTS, fitsRepList);
	}

}
