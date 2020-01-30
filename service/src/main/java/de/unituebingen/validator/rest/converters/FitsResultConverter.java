package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.reports.FitsResultRepresentation;
import de.unituebingen.validator.rest.representations.reports.FitsResultsRepresentation;

public class FitsResultConverter {

	public static FitsResultRepresentation toRepresentation(FitsRecord record) {
		FitsResultRepresentation representation = new FitsResultRepresentation();
		representation.setFileReportId(record.getReport().getId());
		representation.setId(record.getId());
		representation.setTool(record.getTool());
		representation.setToolVersion(record.getToolVersion());
		representation.setFormatName(record.getFormatName());
		representation.setPuid(record.getPuid());
		representation.setMime(record.getMime());
		representation.setWellFormed(record.getWellFormed());
		representation.setValid(record.getValid());
		return representation;
	}

	public static FitsResultsRepresentation toCollectionRepresentation(long totalCount, int page, int pageSize,
			List<FitsRecord> records) {
		FitsResultsRepresentation frep = new FitsResultsRepresentation(totalCount, page, pageSize, records.size());

		List<FitsResultRepresentation> representationList = new ArrayList<>();
		for (FitsRecord fitsRecord : records) {
			representationList.add(FitsResultConverter.toRepresentation(fitsRecord));
		}
		frep.getEmbedded().put(LinkRelations.FITS_RESULTS, representationList);
		return frep;
	}

}
