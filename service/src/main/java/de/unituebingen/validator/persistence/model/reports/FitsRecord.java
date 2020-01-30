package de.unituebingen.validator.persistence.model.reports;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.unituebingen.validator.persistence.model.Persistable;

@Entity
@NamedQueries({
	@NamedQuery(name = FitsRecord.GET_BY_DATE_DESC, query = "SELECT f FROM FitsRecord f WHERE f.report.task.deleted=false ORDER BY f.date DESC"),
	@NamedQuery(name = FitsRecord.GET_BY_FILE_REPORT_ID, query = "SELECT f FROM FitsRecord f WHERE f.report.id=?1 AND f.report.task.deleted=false"),
	@NamedQuery(name = FitsRecord.COUNT, query = "SELECT COUNT(f) FROM FitsRecord f WHERE f.report.task.deleted=false"),
	@NamedQuery(name = FitsRecord.COUNT_BY_FILE_REPORT_ID, query = "SELECT COUNT(f) FROM FitsRecord f WHERE f.report.id=?1 AND f.report.task.deleted=false")
})
public class FitsRecord extends Persistable{
	// Query constants
	public static final String GET_BY_DATE_DESC = "FitsRecord.getByDateDesc";
	public static final String GET_BY_FILE_REPORT_ID = "FitsRecord.getByFileReportId";
	public static final String COUNT_BY_FILE_REPORT_ID = "FitsRecord.CountByFileReportId";
	public static final String COUNT = "FitsRecord.Count";

	@ManyToOne(cascade=CascadeType.MERGE)
	private FileValidationReport report;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
		
	private String tool;
	private String toolVersion;
	private String formatName;
	private String puid;
	private String mime;
	private Boolean wellFormed;
	private Boolean valid;
	
	
	public boolean isDeleted() {
		if (report != null) {
			return report.isDeleted();
		}
		return false;
	}
	
	
	// Setters and Getters

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public Boolean isWellFormed() {
		return wellFormed;
	}

	public void setWellFormed(Boolean wellFormed) {
		this.wellFormed = wellFormed;
	}

	public Boolean isValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public FileValidationReport getReport() {
		return report;
	}


	public void setReport(FileValidationReport report) {
		this.report = report;
	}


	public String getTool() {
		return tool;
	}


	public void setTool(String tool) {
		this.tool = tool;
	}


	public String getToolVersion() {
		return toolVersion;
	}


	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}

	public Boolean getWellFormed() {
		return wellFormed;
	}

	public Boolean getValid() {
		return valid;
	}
	
	

	
}
