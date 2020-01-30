package de.unituebingen.validator.verapdf;

/**
 * This class contains custom pdf validation rules as string constants.
 * Every rule contains a message key to be used for localization of result messages.
 * @author Fabian Hamm
 *
 */
public class PdfValidationPolicies {
	
	/** Message keys to be used for generating localized output messages*/
	public static final String MESSAGE_KEY_TRAILER_ENCRYPT_POLICY = "TRAILER_ENCRYPT_POLICY";
	public static final String MESSAGE_KEY_ENCRYPT_POLICY = "ENCRYPT_POLICY";
	public static final String MESSAGE_KEY_FONTS_POLICY = "FONTS_POLICY";
	public static final String MESSAGE_KEY_MULTIMDIA_POLICY = "MULTIMDIA_POLICY";
	public static final String MESSAGE_KEY_ATTACHMENTS_POLICY = "ATTACHMENTS_POLICY";
	public static final String MESSAGE_KEY_FILES_POLICY = "FILES_POLICY";
	public static final String MESSAGE_KEY_PDFA_VALID_POLICY = "PDFA_VALID_POLICY";
	public static final String MESSAGE_KEY_PDFA_VALID_ENCRYPTED_POLICY = "PDFA_VALID_ENCRYPTED_POLICY";
	
	
	public static final String RULE_TRAILER_ENCRYPT =  
			"    <sch:pattern name=\"Disallow encrypt in trailer dictionary\"> " + 
			"        <sch:rule context=\"/report/jobs/job/featuresReport/documentSecurity\"> " + 
			"            <sch:assert test=\"not(encryptMetadata = 'true')\">" + MESSAGE_KEY_TRAILER_ENCRYPT_POLICY + "</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern>     ";
	
	public static final String RULE_ENCRYPT =  
			"	<sch:pattern name=\"Disallow other forms of encryption (Check for 'encrypted' in VeraPDF exception messages)\"> " + 
			"        <sch:rule context=\"/report/jobs/job/taskResult/exceptionMessage\"> " + 
			"            <sch:assert test=\"not(contains(.,'encrypted'))\">" + MESSAGE_KEY_ENCRYPT_POLICY + "</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern>";
	
	public static final String RULE_EMBEDDED_FONTS =
			"    <sch:pattern name=\"Fonts must be embedded\"> " + 
			"        <sch:rule context=\"/report/jobs/job/featuresReport/documentResources/fonts/font/fontDescriptor\"> " + 
			"   	        <sch:let name=\"currentName\" value=\"preceding-sibling::baseFont/text()\"/> " + 
			"            <sch:assert test=\"not(embedded = 'false')\">" + MESSAGE_KEY_FONTS_POLICY + ": <sch:value-of select=\"$currentName\"/></sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern> ";
	
	public static final String RULE_MULTIMEDIA = 
			"    <sch:pattern name=\"Multimedia not allowed\"> " + 
			"        <sch:rule context=\"/report/jobs/job/featuresReport/annotations/annotation\"> " + 
			"            <sch:assert test=\"not(subType='Screen')\">" + MESSAGE_KEY_MULTIMDIA_POLICY + "</sch:assert> " + 
			"            <sch:assert test=\"not(subType='Movie')\">" + MESSAGE_KEY_MULTIMDIA_POLICY + "</sch:assert> " + 
			"            <sch:assert test=\"not(subType='Sound')\">" + MESSAGE_KEY_MULTIMDIA_POLICY + "</sch:assert> " + 
			"            <sch:assert test=\"not(subType='3D')\">" + MESSAGE_KEY_MULTIMDIA_POLICY + "</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern> ";
	
	public static final String RULE_ATTACHMENTS = 
			"    <sch:pattern name=\"File attachments not allowed\"> " + 
			"        <sch:rule context=\"/report/jobs/job/featuresReport/annotations/annotation\"> " + 
			"            <sch:assert test=\"not(subType='FileAttachment')\">" + MESSAGE_KEY_ATTACHMENTS_POLICY + "</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern> ";
	
	public static final String RULE_EMBEDDED_FILES = 
			"    <sch:pattern name=\"Embedded files not allowed\"> " + 
			"        <sch:rule context=\"/report/jobs/job/featuresReport/embeddedFiles\"> " + 
			"            <sch:assert test=\"not('embeddedFile')\">" + MESSAGE_KEY_FILES_POLICY + "</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern> " ;
	
	public static final String RULE_DOCUMENT_PARSABLE = 
			" 	<sch:pattern name=\"Document must be parsable (poor man's proxy for canonical PDF validation).\"> " + 
			"        <sch:rule context=\"/report/jobs/job/taskResult\"> " + 
			"            <sch:assert test=\"not(@type='PARSE' and @isSuccess='false')\">Document not parsable</sch:assert> " + 
			"        </sch:rule> " + 
			"    </sch:pattern> " ;
	
}
