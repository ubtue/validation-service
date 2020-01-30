package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.rest.representations.FileRepresentation;
import de.unituebingen.validator.rest.representations.FilesRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;

public class FileUploadConverter {

	/**
	 * Creates a {@link FileRepresentation} from a {@link FileUpload}.
	 * 
	 * @param fileUpload
	 *            the upload.
	 * @return
	 */
	public static FileRepresentation toRepresentation(FileUpload fileUpload) {
		FileRepresentation rep = new FileRepresentation();
		rep.setFileName(fileUpload.getFileName());
		rep.setPath(fileUpload.getFilePath());
		rep.setId(fileUpload.getId());
		rep.setSize(fileUpload.getSize());
		rep.setCreationDate(fileUpload.getCreationDate().getTime());
		if (fileUpload.getBatch() != null)
			rep.setBatchId(fileUpload.getBatch().getId());
		return rep;
	}

	/**
	 * Creates a {@link FilesRepresentation}.
	 * 
	 * @param totalCount
	 *            The amount of results in total.
	 * @param pageIndex
	 *            The index of the page.
	 * @param pageSize
	 *            The number of results per page in general.
	 * @param fileUploads
	 *            the file uploads to be embedded.
	 */
	public static FilesRepresentation toCollectionRepresentation(long totalCount, int pageIndex, int pageSize,
			List<FileUpload> fileUploads) {
		FilesRepresentation filesRep = new FilesRepresentation(totalCount, pageIndex, pageSize, fileUploads.size());
		List<FileRepresentation> fileRepList = new ArrayList<>();
		for (FileUpload fileUpload : fileUploads) {
			fileRepList.add(FileUploadConverter.toRepresentation(fileUpload));
		}
		filesRep.getEmbedded().put(LinkRelations.FILE_UPLOADS, fileRepList);
		return filesRep;
	}

}
