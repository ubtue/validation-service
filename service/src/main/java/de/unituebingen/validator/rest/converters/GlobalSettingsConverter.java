package de.unituebingen.validator.rest.converters;

import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;
import de.unituebingen.validator.rest.representations.SetupRepresentation;

public class GlobalSettingsConverter {

	/**
	 * Creates a {@link GlobalSettings} instance from a {@link SetupRepresentation}
	 * 
	 * @param representation
	 *            The Representation.
	 * @return
	 */
	public static GlobalSettings toEntity(SetupRepresentation representation) {
		GlobalSettings global = new GlobalSettings();
		global.setId(representation.getId());
		global.setConcurrenThreadsPerTask(representation.getThreadsPerTask());
		global.setConcurrentTasks(representation.getParallelTasks());
		global.setPageSize(representation.getPageSize());
		global.setMessageTranslations(representation.getMessageTranslations());
		global.setAutoDeleteBatches(representation.isAutoDeleteBatches());
		global.setAutoDeleteHours(representation.getAutoDeleteHours());
		return global;
	}

	/**
	 * Creates a {@link SetupRepresentation} from a {@link GlobalSettings} instance.
	 * 
	 * @param settings
	 *            The settings instance.
	 * @return
	 */
	public static SetupRepresentation toRepresentation(GlobalSettings settings) {
		SetupRepresentation rep = new SetupRepresentation();
		rep.setId(settings.getId());
		rep.setPageSize(settings.getPageSize());
		rep.setParallelTasks(settings.getConcurrentTasks());
		rep.setThreadsPerTask(settings.getConcurrenThreadsPerTask());
		rep.setMessageTranslations(settings.getMessageTranslations());
		rep.setAutoDeleteBatches(settings.isAutoDeleteBatches());
		rep.setAutoDeleteHours(settings.getAutoDeleteHours());
		return rep;
	}

}
