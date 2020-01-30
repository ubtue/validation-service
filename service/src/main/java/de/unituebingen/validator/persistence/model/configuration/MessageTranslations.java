package de.unituebingen.validator.persistence.model.configuration;

import java.util.HashMap;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Holds the translations for the veraPDF result messages seen in DSpace and the web ui;
 * @author Fabian Hamm
 *
 */
@Embeddable
@XmlRootElement
@JsonPropertyOrder({ 
	"encryptTranslations",
	"trailerEncryptTranslations",
	"pdfAEncryptedTranslations",
	"pdfATranslations", 
	"fontsTranslations",
	"multimediaTranslations",
	"attachmentsTranslations",
	"filesTranslations"
})
public class MessageTranslations {
	HashMap<String, String> pdfATranslations = new HashMap<>();
	HashMap<String, String> pdfAEncryptedTranslations = new HashMap<>();
	HashMap<String, String> encryptTranslations = new HashMap<>();
	HashMap<String, String> trailerEncryptTranslations = new HashMap<>();
	HashMap<String, String> fontsTranslations = new HashMap<>();
	HashMap<String, String> multimediaTranslations = new HashMap<>();
	HashMap<String, String> attachmentsTranslations = new HashMap<>();
	HashMap<String, String> filesTranslations = new HashMap<>();
	
	
	public HashMap<String, String> getTrailerEncryptTranslations() {
		return trailerEncryptTranslations;
	}
	
	public void setTrailerEncryptTranslations(HashMap<String, String> trailerEncryptTranslations) {
		this.trailerEncryptTranslations = trailerEncryptTranslations;
	}
	
	public HashMap<String, String> getEncryptTranslations() {
		return encryptTranslations;
	}
	
	public void setEncryptTranslations(HashMap<String, String> encryptTranslations) {
		this.encryptTranslations = encryptTranslations;
	}
	
	public HashMap<String, String> getFontsTranslations() {
		return fontsTranslations;
	}
	
	public void setFontsTranslations(HashMap<String, String> fontsTranslations) {
		this.fontsTranslations = fontsTranslations;
	}
	
	public HashMap<String, String> getMultimediaTranslations() {
		return multimediaTranslations;
	}
	
	public void setMultimediaTranslations(HashMap<String, String> multimediaTranslations) {
		this.multimediaTranslations = multimediaTranslations;
	}
	
	public HashMap<String, String> getAttachmentsTranslations() {
		return attachmentsTranslations;
	}
	
	public void setAttachmentsTranslations(HashMap<String, String> attachmentsTranslations) {
		this.attachmentsTranslations = attachmentsTranslations;
	}
	
	public HashMap<String, String> getFilesTranslations() {
		return filesTranslations;
	}
	public void setFilesTranslations(HashMap<String, String> filesTranslations) {
		this.filesTranslations = filesTranslations;
	}
	
	public HashMap<String, String> getPdfATranslations() {
		return pdfATranslations;
	}
	
	public void setPdfATranslations(HashMap<String, String> pdfATranslations) {
		this.pdfATranslations = pdfATranslations;
	}
	
	public HashMap<String, String> getPdfAEncryptedTranslations() {
		return pdfAEncryptedTranslations;
	}
	
	public void setPdfAEncryptedTranslations(HashMap<String, String> pdfAEncryptedTranslations) {
		this.pdfAEncryptedTranslations = pdfAEncryptedTranslations;
	}
	
	
}
