package de.unituebingen.validator.persistence.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;


@Entity
@NamedQueries ({
	@NamedQuery(name = FileUpload.SELECT_BY_BATCH_STARTING_AFTER_UPLOAD_ID_ORDER_BY_UPLOAD_ID_ASC_, 
		query = "SELECT u FROM FileUpload u WHERE u.batch = ?1 AND u.id > ?2 ORDER BY u.id ASC"),
	@NamedQuery(name = FileUpload.COUNT_BY_BATCH_AND_NAME_LIKE_PATTERN, 
		query = "SELECT COUNT(u) FROM FileUpload u WHERE u.batch = ?1 and UPPER(u.fileName) LIKE UPPER(?2)")
})
public class FileUpload extends Persistable implements Serializable{
	
	private static final long serialVersionUID = 5860383099542654142L;
	
	// Query constants
	public static final String SELECT_BY_BATCH_STARTING_AFTER_UPLOAD_ID_ORDER_BY_UPLOAD_ID_ASC_ = "FileUpload.selectByBatchStartingAfterUploadIdOrderByUploadIdAsc";
	public static final String COUNT_BY_BATCH_AND_NAME_LIKE_PATTERN = "FileUpload.CountByBatchAndNameLikePattern";
	
	@OneToMany(cascade={CascadeType.PERSIST}, mappedBy="fileUpload", orphanRemoval=false)
	private List<FileValidationReport> validationReports = new ArrayList<>();
	
	@NotNull
	String fileName;
	
	@NotNull
	@Column(length=500)
	String filePath;
	
	@ManyToOne()
	Batch batch;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	private Long size;

	
	public void preRemove() {
		if(batch != null)
			this.batch.getFiles().remove(this);
		
		for (Iterator<FileValidationReport> iterator = validationReports.iterator(); iterator.hasNext();) {
			FileValidationReport fileValidationReport = (FileValidationReport) iterator.next();
			fileValidationReport.setFileUpload(null);
			iterator.remove();			
		}
	}
	
	public void addValidationReport(FileValidationReport validationReport) {
		validationReports.add(validationReport);
		validationReport.setFileUpload(this);
	}
	
	
	public boolean isDeleted() {
		if (batch != null) {
			return batch.isDeleted();
		}
		return false;
	}
	
	
	// Generated Setters and Getters
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Batch getBatch() {
		return batch;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}


	public List<FileValidationReport> getValidationReports() {
		return validationReports;
	}

	
}
