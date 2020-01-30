package de.unituebingen.validator.fits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.unituebingen.validator.persistence.model.reports.FitsRecord;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

/**
 * Parser for fits xml result file.
 *
 */
public class FitsResultParser {

	@Deprecated
	public static List<String> getErrorMessages(FitsOutput output) {
		List<String> errorMessages = new ArrayList<>();
		List<Exception> exceptions = output.getCaughtExceptions();
		for (Exception exception : exceptions) {
			errorMessages.add(exception.getMessage());
		}

		return errorMessages;
	}

	public static List<FitsRecord> fromFitsOutput(FitsOutput output) {
		List<FitsRecord> recordList = new ArrayList<>();

		// Iterate through identities and create record per identity tool
		List<FitsIdentity> identities = output.getIdentities();
		for (FitsIdentity identity : identities) {
			List<ToolInfo> toolsInfos = identity.getReportingTools();

			for (ToolInfo toolInfo : toolsInfos) {
				FitsRecord record = new FitsRecord();
				record.setMime(identity.getMimetype());
				record.setTool(toolInfo.getName());
				record.setToolVersion(toolInfo.getVersion());
				record.setFormatName(identity.getFormat());

				List<ExternalIdentifier> externalIdentifiers = identity.getExternalIdentifiers();
				for (ExternalIdentifier externalIdentifier : externalIdentifiers) {
					if (externalIdentifier.getName().equals("puid")) {
						if (externalIdentifier.getToolInfo().getName().equals(record.getTool()))
							record.setPuid(externalIdentifier.getValue());
					}
				}
				recordList.add(record);
			}
		}

		List<FitsMetadataElement> elements = output.getFileStatusElements();
		for (FitsMetadataElement fitsMetadataElement : elements) {
			if (fitsMetadataElement.getName().equals("well-formed")) {
				for (FitsRecord record : recordList) {
					if (fitsMetadataElement.getReportingToolName().equals(record.getTool())
							&& fitsMetadataElement.getReportingToolVersion().equals(record.getToolVersion())) {
						record.setWellFormed(Boolean.valueOf(fitsMetadataElement.getValue()));
					}
				}
			}

			if (fitsMetadataElement.getName().equals("valid")) {
				for (FitsRecord record : recordList) {
					if (fitsMetadataElement.getReportingToolName().equals(record.getTool())
							&& fitsMetadataElement.getReportingToolVersion().equals(record.getToolVersion())) {
						record.setValid(Boolean.valueOf(fitsMetadataElement.getValue()));
					}
				}
			}
		}

		for (FitsRecord record : recordList) {
			record.setDate(new Date());
		}

		return recordList;
	}

	public static boolean containsRecordWithMimeType(String mimeType, List<FitsRecord> fitsRecords) {
		if (mimeType == null)
			return false;

		for (FitsRecord fitsRecord : fitsRecords) {
			if (fitsRecord.getMime().equals(mimeType))
				return true;
		}
		return false;
	}

}
