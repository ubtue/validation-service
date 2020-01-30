package de.unituebingen.validator.persistence.model.configuration;

import javax.persistence.Embeddable;

@Embeddable
public class VeraPdfPolicyOptions {
	
	private boolean disallowEncryptInTrailer;
	private boolean disallowEncryption;
	private boolean disallowEmbeddedFonts;
	private boolean disallowEmbeddedFiles;
	private boolean disallowFileAttachments;
	private boolean disallowMultimediaAnnotations;
	private boolean disallowNonParseableDocuments;
	
	public boolean isDisallowEncryptInTrailer() {
		return disallowEncryptInTrailer;
	}
	
	public void setDisallowEncryptInTrailer(boolean disallowEncryptInTrailer) {
		this.disallowEncryptInTrailer = disallowEncryptInTrailer;
	}
	
	public boolean isDisallowEncryption() {
		return disallowEncryption;
	}
	
	public void setDisallowEncryption(boolean disallowOtherFormsOfEncrypt) {
		this.disallowEncryption = disallowOtherFormsOfEncrypt;
	}
	
	public boolean isDisallowEmbeddedFonts() {
		return disallowEmbeddedFonts;
	}
	
	public void setDisallowEmbeddedFonts(boolean disallowEmbeddedFonts) {
		this.disallowEmbeddedFonts = disallowEmbeddedFonts;
	}
	
	public boolean isDisallowEmbeddedFiles() {
		return disallowEmbeddedFiles;
	}
	
	public void setDisallowEmbeddedFiles(boolean disallowEmbeddedFiles) {
		this.disallowEmbeddedFiles = disallowEmbeddedFiles;
	}
	
	public boolean isDisallowFileAttachments() {
		return disallowFileAttachments;
	}
	
	public void setDisallowFileAttachments(boolean disallowFileAttachments) {
		this.disallowFileAttachments = disallowFileAttachments;
	}
	
	public boolean isDisallowMultimediaAnnotations() {
		return disallowMultimediaAnnotations;
	}
	
	public void setDisallowMultimediaAnnotations(boolean disallowMultimediaAnnotations) {
		this.disallowMultimediaAnnotations = disallowMultimediaAnnotations;
	}

	public boolean isDisallowNonParseableDocuments() {
		return disallowNonParseableDocuments;
	}

	public void setDisallowNonParseableDocuments(boolean disallowNonParseableDocuments) {
		this.disallowNonParseableDocuments = disallowNonParseableDocuments;
	}
	
	
}
