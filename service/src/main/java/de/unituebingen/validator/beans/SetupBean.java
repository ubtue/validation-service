package de.unituebingen.validator.beans;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.unituebingen.validator.common.qualifiers.Cancel;
import de.unituebingen.validator.common.qualifiers.Task;
import de.unituebingen.validator.persistence.model.ValidationStatus;
import de.unituebingen.validator.persistence.model.ValidationTask;
import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;
import de.unituebingen.validator.persistence.model.configuration.MessageTranslations;
import de.unituebingen.validator.persistence.model.reports.FileValidationReport;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.persistence.model.user.User;
import de.unituebingen.validator.persistence.repository.GlobalSettingsRepository;
import de.unituebingen.validator.persistence.repository.UserRepository;
import de.unituebingen.validator.persistence.repository.ValidationTaskRepository;
import de.unituebingen.validator.rest.authentication.PasswordUtils;

/**
 * Bean managing global application settings and startup/shutdown behavior.
 *
 */
@LocalBean
@Singleton
@Startup
public class SetupBean {

	@Inject
	GlobalSettingsRepository settingsRepository;
	@Inject
	ValidationTaskRepository taskRepository;
	@Inject
	UserRepository userRepository;
	@Inject
	Logger logger;
	@Inject
	@Task
	Event<Long> taskStartEvent;
	@Inject
	@Task
	@Cancel
	Event<Long> unscheduleTaskEvent;

	private GlobalSettings globalSettings;

	/**
	 * Create global settings record if not present in database
	 */
	@PostConstruct
	private void init() {
		// load or create settings
		try {
			globalSettings = settingsRepository.find();
		} catch (NoResultException e) {
			logger.log(Level.SEVERE, "Could not load global settings from database. Create new default settings ...");
			globalSettings = new GlobalSettings();
			globalSettings.setConcurrentTasks(2);
			globalSettings.setConcurrenThreadsPerTask(2);
			globalSettings.setEmbeddedResources(3);
			globalSettings.setPageSize(7);
			globalSettings.setAutoDeleteBatches(true);
			globalSettings.setAutoDeleteHours(48);

			// German and english language codes;
			final String deCode = "de";
			final String enCode = "en";

			MessageTranslations translations = new MessageTranslations();
			globalSettings.setMessageTranslations(translations);

			HashMap<String, String> tempMap = translations.getAttachmentsTranslations();
			tempMap.put(enCode, "File attachments in PDF files are not allowed");
			tempMap.put(deCode, "Dateianhänge in PDF-Dateien sind nicht erlaubt");

			tempMap = translations.getEncryptTranslations();
			tempMap.put(enCode, "PDF file seems to be encrypted");
			tempMap.put(deCode, "PDF-Datei scheint kennwortgeschützt zu sein. Bitte Kennwortschutz entfernen!");

			tempMap = translations.getFilesTranslations();
			tempMap.put(enCode, "PDF files may not contain embedded files");
			tempMap.put(deCode, "PDF-Dateien dürfen keine eingebetteten Dateien enthalten");

			tempMap = translations.getFontsTranslations();
			tempMap.put(enCode, "Font is not embedded");
			tempMap.put(deCode, "Font ist nich eingebettet");

			tempMap = translations.getMultimediaTranslations();
			tempMap.put(enCode, "Multimedia content is not allowed");
			tempMap.put(deCode, "Multimediainhalte sind nicht erlaubt");

			tempMap = translations.getPdfAEncryptedTranslations();
			tempMap.put(enCode, "File is no valid PDF/A document (File seems to be encrypted)");
			tempMap.put(deCode, "Datei ist kein valides PDF/A-Dokument (Die Datei scheint kennwortgeschützt zu sein)");

			tempMap = translations.getPdfATranslations();
			tempMap.put(enCode, "File is no valid PDF/A document");
			tempMap.put(deCode, "Datei ist kein valides PDF/A-Dokument");

			tempMap = translations.getTrailerEncryptTranslations();
			tempMap.put(enCode, "'Encrypt' keyword may not appear in trailer dictionary of PDF document");
			tempMap.put(deCode,
					"Passwortgeschützte Berechtigungen sind nicht erlaubt. Bitte Kennwortschutz entfernen!");
			settingsRepository.persist(globalSettings);

			User admin = new User();
			admin.setLogin("admin");
			admin.setPasswordDigest(PasswordUtils.digestPassword("admin"));
			admin.setRole(Role.ADMIN);

			User user = new User();
			user.setLogin("user");
			user.setPasswordDigest(PasswordUtils.digestPassword("user"));
			user.setRole(Role.USER);

			userRepository.persist(admin);
			userRepository.persist(user);
		}

		resetStuckValidationTasks();
		taskStartEvent.fire(0l);
	}

	@PreDestroy
	private void shutdown() {
		settingsRepository.merge(globalSettings);

		List<ValidationTask> scheduledTasks = taskRepository.findProcessingOrQueuedOrdeByCreationAsc();
		for (ValidationTask validationTask : scheduledTasks) {
			unscheduleTaskEvent.fire(validationTask.getId());
		}

	}

	/**
	 * Resets all tasks marked as processing to queued status. All progress will be
	 * lost.
	 */
	public void resetStuckValidationTasks() {
		List<ValidationTask> tasks = taskRepository.findProcessing();
		for (ValidationTask validationTask : tasks) {
			for (FileValidationReport report : validationTask.getFileValidationReports()) {
				report.preRemove();
			}
			validationTask.getFileValidationReports().clear();
			validationTask.setValidationStatus(ValidationStatus.QUEUED);
		}
	}

	/**
	 * Get global application settings.
	 * 
	 * @return The settings.
	 */
	public GlobalSettings getGlobalSettings() {
		return settingsRepository.find();
	}

	/**
	 * Update global application settings.
	 * 
	 * @param globalSettings
	 *            the new settings.
	 */
	public void updateGlobalSettings(GlobalSettings globalSettings) {
		settingsRepository.merge(globalSettings);
	}

}
