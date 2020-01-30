package de.unituebingen.validator.rest.converters;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.unituebingen.validator.persistence.model.configuration.VeraPdfConfiguration;
import de.unituebingen.validator.rest.config.LinkDeserializer;
import de.unituebingen.validator.rest.config.LinkSerializer;
import de.unituebingen.validator.rest.representations.ConfigurationRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.VeraPdfConfigurationRepresentation;

public class VeraPdfConfigurationConverter {

	/**
	 * Creates a {@link VeraPdfConfigurationRepresentation} from a
	 * {@link VeraPdfConfiguration}
	 * 
	 * @param config
	 *            the {@link VeraPdfConfiguration}
	 * @param ConfigId
	 *            the id of the corresponding
	 * @return
	 */
	public static final VeraPdfConfigurationRepresentation toRepresentation(VeraPdfConfiguration config) {
		VeraPdfConfigurationRepresentation rep = new VeraPdfConfigurationRepresentation();
		rep.setConfigurationId(config.getConfiguration().getId());
		rep.setId(config.getId());
		rep.setFailedChecksPerRuleDisplayed(config.getFailedChecksPerRuleDisplayed());
		rep.setFailedChecksThreshold(config.getFailedChecksThreshold());
		rep.setReportPassedRules(config.isReportPassedRules());
		rep.setValidationProfile(config.getValidationProfile());
		rep.setExecutionMode(config.getExecutionMode());
		rep.setFailOnInvalidPdfA(config.isFailOnInvalidPdfA());
		rep.setPdfPolicies(config.getPdfPolicies());
		return rep;
	}

	/**
	 * Deserializes an embedded verapdf configuration.
	 * 
	 * @return verapdf configuration or null.
	 */
	public static VeraPdfConfigurationRepresentation deserializeEmbeddedVeraPdfConfiguration(
			ConfigurationRepresentation config) {
		if (config.getEmbedded().get(LinkRelations.VERAPDF_SETUP) != null) {
			ObjectMapper mapper = new ObjectMapper();
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addSerializer(Link.class, new LinkSerializer());
			simpleModule.addDeserializer(Link.class, new LinkDeserializer());
			mapper.registerModule(simpleModule);

			try {
				VeraPdfConfigurationRepresentation verRep = mapper.convertValue(
						config.getEmbedded().get(LinkRelations.VERAPDF_SETUP),
						VeraPdfConfigurationRepresentation.class);
				return verRep;
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

	public static void updateEntity(VeraPdfConfiguration config, VeraPdfConfigurationRepresentation rep) {
		config.setExecutionMode(rep.getExecutionMode());
		config.setFailedChecksPerRuleDisplayed(rep.getFailedChecksPerRuleDisplayed());
		config.setFailedChecksThreshold(rep.getFailedChecksThreshold());
		config.setFailOnInvalidPdfA(rep.isFailOnInvalidPdfA());
		config.setReportPassedRules(rep.isReportPassedRules());
		config.setValidationProfile(rep.getValidationProfile());
		config.setPdfPolicies(rep.getPdfPolicies());
	}

}
