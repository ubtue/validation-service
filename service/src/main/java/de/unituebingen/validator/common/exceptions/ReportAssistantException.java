package de.unituebingen.validator.common.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ReportAssistantException extends Exception {

	private static final long serialVersionUID = 3278918111665023041L;

	public ReportAssistantException() {
		super();
	}

	public ReportAssistantException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReportAssistantException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReportAssistantException(String message) {
		super(message);
	}

	public ReportAssistantException(Throwable cause) {
		super(cause);
	}

}
