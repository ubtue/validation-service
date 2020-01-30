package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.unituebingen.validator.persistence.model.configuration.ProcessorConfiguration;
import de.unituebingen.validator.rest.representations.ConfigurationRepresentation;
import de.unituebingen.validator.rest.representations.ConfigurationsRepresentation;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.VeraPdfConfigurationRepresentation;

public class ProcessorConfigurationConverter {

	/**
	 * Creates an new {@link ConfigurationRepresentation} from a
	 * {@link ProcessorConfiguration}.
	 * 
	 * @param config
	 *            The configuration to be transformed to a representation.
	 * @return
	 */
	public static final ConfigurationRepresentation toRepresentation(ProcessorConfiguration config) {
		ConfigurationRepresentation rep = new ConfigurationRepresentation();
		rep.setId(config.getId());
		rep.setVeraPdfTimeOut(config.getVeraPdfTimeOut());
		rep.setFitsTimeOut(config.getFitsTimeOut());
		rep.setFitsEnabled(config.isFitsActive());
		rep.setDescription(config.getDescription());
		rep.setCreationDate(config.getCreationDate().getTime());
		rep.setVeraPdfMaxHeapSize(config.getVeraPdfMaxHeapSize());
		rep.setPublicIdentifier(config.getPublicIdentifier());
		if (config.getVeraPdfConfiguration() != null) {
			rep.getEmbedded().put(LinkRelations.VERAPDF_SETUP,
					VeraPdfConfigurationConverter.toRepresentation(config.getVeraPdfConfiguration()));
		}
		return rep;
	}

	/**
	 * Updates the state of a processor configuration with the state of this
	 * representation.
	 * 
	 * @param procCon
	 *            the processor configuration.
	 */
	public static void updateEntity(ProcessorConfiguration procCon, ConfigurationRepresentation representation) {
		procCon.setCreationDate(new Date(representation.getCreationDate()));
		procCon.setDescription(representation.getDescription());
		procCon.setFitsActive(representation.isFitsEnabled());
		procCon.setVeraPdfTimeOut(representation.getVeraPdfTimeOut());
		procCon.setFitsTimeOut(representation.getFitsTimeOut());
		procCon.setVeraPdfMaxHeapSize(representation.getVeraPdfMaxHeapSize());
		procCon.setPublicIdentifier(representation.getPublicIdentifier());
	}

	public static ConfigurationsRepresentation toCollectionRepresentation(long totalCount, int pageIndex, int pageSize,
			List<ProcessorConfiguration> configurations) {
		ConfigurationsRepresentation confsRep = new ConfigurationsRepresentation(totalCount, pageIndex, pageSize,
				configurations.size());

		List<ConfigurationRepresentation> configRepList = new ArrayList<>();
		for (ProcessorConfiguration procCon : configurations) {
			configRepList.add(ProcessorConfigurationConverter.toRepresentation(procCon));
		}
		confsRep.getEmbedded().put(LinkRelations.CONFIGURATIONS, configRepList);
		return confsRep;
	}
}
