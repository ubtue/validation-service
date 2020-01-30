package de.unituebingen.validator.tasks;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import de.unituebingen.validator.beans.PathCleanUpBean;
import de.unituebingen.validator.common.Sequencer;
import de.unituebingen.validator.persistence.model.Batch;
import de.unituebingen.validator.persistence.model.FileUpload;
import de.unituebingen.validator.rest.representations.FileRepresentation;

public class FileUploadTask implements Runnable {

	@PersistenceContext(unitName = "dspacevalidator-psu")
	EntityManager em;

	@Inject
	PathCleanUpBean fileSystem;

	@Resource
	UserTransaction transaction;

	@Resource(name = "baseDirectory")
	private String baseDirectory;

	@Inject
	Logger logger;

	private AsyncResponse response;
	private List<FormDataBodyPart> files;
	private Long batchId;
	private String baseLink;
	private boolean batchUpload;

	@Override
	public void run() {
		Batch batch = null;
		List<FileUpload> addedUploads = new ArrayList<>();
		try {
			if (files == null)
				response.resume(new BadRequestException("Form data param 'files' not found"));

			transaction.begin();

			if (batchUpload) {
				batch = em.find(Batch.class, batchId);
				if (batch == null || batch.isDeleted()) {
					transaction.rollback();
					response.resume(new NotFoundException("Batch not found"));
				}
			}

			List<FileRepresentation> fileRepList = new ArrayList<>();
			for (FormDataBodyPart file : files) {
				UUID uuid = java.util.UUID.randomUUID();
				FileUpload fileUpload = new FileUpload();

				try (InputStream inputStream = file.getValueAs(InputStream.class);) {

					if (file.getContentDisposition().getFileName() == null)
						throw new BadRequestException("File name not set in content disposition");

					String fileName = new String(file.getContentDisposition().getFileName().getBytes("ISO-8859-1"),
							"UTF-8");
					String filePath;

					if (batchUpload) {
						filePath = batch.getFolderPath() + File.separator + String.valueOf(Sequencer.getSequence())
								+ "_" + uuid.toString() + File.separator + fileName;
					} else {
						filePath = baseDirectory + File.separator + Sequencer.getSequence()
								+ UUID.randomUUID().toString() + File.separator
								+ String.valueOf(Sequencer.getSequence()) + "_" + uuid.toString() + File.separator
								+ fileName;
					}

					File tmpFile = new File(filePath);
					fileUpload.setFileName(fileName);
					fileUpload.setFilePath(filePath);
					fileUpload.setBatch(batch);
					fileUpload.setCreationDate(new Date());
					addedUploads.add(fileUpload);
					FileUtils.copyInputStreamToFile(inputStream, tmpFile);
					fileUpload.setSize(tmpFile.length());
					file.cleanup();
				}
				em.persist(fileUpload);
				em.flush();

				if (batchUpload)
					batch.addFileUpload(fileUpload);

				FileRepresentation fRep = new FileRepresentation();
				fRep.setId(fileUpload.getId());
				fRep.setPath(fileUpload.getFilePath());
				fRep.setFileName(fileUpload.getFileName());
				fRep.setCreationDate(fileUpload.getCreationDate().getTime());
				fRep.setSize(fileUpload.getSize());

				if (batchUpload)
					fRep.setBatchId(batch.getId());

				fileRepList.add(fRep);
			}
			transaction.commit();
			response.resume(Response.ok(fileRepList).build());

		} catch (Exception e) {
			if (batch != null) {
				for (FileUpload upload : addedUploads) {
					fileSystem.removePath(upload.getFilePath());
				}
			}
			logger.log(Level.SEVERE, e.getMessage(), e);
			try {
				transaction.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				logger.log(Level.FINER, "Transaction rollback failed", e1);
			}
			response.resume(new ServerErrorException("upload error", 500));
		}
	}

	public AsyncResponse getResponse() {
		return response;
	}

	public void setResponse(AsyncResponse response) {
		this.response = response;
	}

	public List<FormDataBodyPart> getFiles() {
		return files;
	}

	public void setFiles(List<FormDataBodyPart> files) {
		this.files = files;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public FileUploadTask(AsyncResponse response, List<FormDataBodyPart> files, Long batchId, String baseLink) {
		super();
		this.response = response;
		this.files = files;
		this.batchId = batchId;
		this.baseLink = baseLink;
	}

	public FileUploadTask() {
		super();
	}

	public String getBaseLink() {
		return baseLink;
	}

	public void setBaseLink(String baseLink) {
		this.baseLink = baseLink;
	}

	public boolean isBatchUpload() {
		return batchUpload;
	}

	public void setBatchUpload(boolean batchUpload) {
		this.batchUpload = batchUpload;
	}

}
