package de.unituebingen.validator.persistence.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A Batch of files and associated validation tasks.
 *
 */

@Entity
@NamedQueries({
		@NamedQuery(name = Batch.COUNT_FILE_UPLOADS_FOR_BATCH_ID, query = "SELECT size(b.files) FROM Batch b WHERE b.deleted=false AND b.id=?1"),
		@NamedQuery(name = Batch.COUNT_ALL_WITH_NAME_PATTERN, query = "SELECT Count(b) from Batch b WHERE UPPER(b.name) LIKE UPPER(?1) AND b.deleted=false") })
public class Batch extends Persistable {
	// Query constants
	public static final String COUNT_FILE_UPLOADS_FOR_BATCH_ID = "Batch.countFileUploadsForBatchId";
	public static final String COUNT_ALL_WITH_NAME_PATTERN = "Batch.countAllWithNamePattern";

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true, mappedBy = "batch")
	private List<FileUpload> files = new ArrayList<>();

	@OneToMany(cascade = { CascadeType.PERSIST }, orphanRemoval = true, mappedBy = "batch")
	private List<ValidationTask> validationTasks;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	private String folderPath;
	private String name;
	private boolean deleted;

	public void addFileUpload(FileUpload fileUpload) {
		files.add(fileUpload);
		fileUpload.setBatch(this);
	}

	public void addValidationTask(ValidationTask task) {
		validationTasks.add(task);
		task.setBatch(this);
	}

	public void preRemove() {
		for (Iterator<FileUpload> iterator = files.iterator(); iterator.hasNext();) {
			FileUpload fileUpload = (FileUpload) iterator.next();
			fileUpload.setBatch(null);
			fileUpload.preRemove();
			iterator.remove();
		}

		for (Iterator<ValidationTask> iterator = validationTasks.iterator(); iterator.hasNext();) {
			ValidationTask task = (ValidationTask) iterator.next();
			task.setBatch(null);
			task.preRemove();
			iterator.remove();
		}
	}

	// Generated Setters and Getters

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<FileUpload> getFiles() {
		return files;
	}

	public List<ValidationTask> getValidationTasks() {
		return validationTasks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}

}
