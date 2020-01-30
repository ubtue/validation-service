package de.unituebingen.validator.beans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import de.unituebingen.validator.common.qualifiers.Files;
import de.unituebingen.validator.common.qualifiers.Remove;

/**
 * Bean for deleting paths on the file system.
 *
 */
@Singleton
public class PathCleanUpBean {

	@Inject
	Logger logger;

	@Asynchronous
	@Lock(LockType.READ)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void removePath(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Remove @Files String path) {
		try {
			FileUtils.forceDelete(new File(path));
		} catch (IOException | NullPointerException e) {
			logger.log(Level.WARNING, "Couldn't delete file/path: " + path);
		}
	}

	@Asynchronous
	@Lock(LockType.READ)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void removePaths(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @Remove @Files ArrayList<String> filePathList) {
		for (String path : filePathList) {
			try {
				FileUtils.forceDelete(new File(path));
			} catch (IOException | NullPointerException e) {
				logger.log(Level.WARNING, "Couldn't delete file/path: " + path);
			}
		}
	}

}
