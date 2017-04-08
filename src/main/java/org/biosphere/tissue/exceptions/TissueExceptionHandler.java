package org.biosphere.tissue.exceptions;

import org.biosphere.tissue.utils.Logger;

public class TissueExceptionHandler {
	public TissueExceptionHandler() {
		super();
	}

	public static void handleUnrecoverableGenericException(Exception e, String module, String message)
			throws CellException {
		CellException cellException = new CellException(message, module, e);
		e.printStackTrace();
		throw cellException;
	}

	public static void handleGenericException(Exception e, String module, String message) {
		Logger logger = new Logger();
		logger.exception(module, message + " Exception message(" + e.getMessage() + ")");
		e.printStackTrace();
	}
}
