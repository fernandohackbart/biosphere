package org.biosphere.tissue.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Logger logger = LoggerFactory.getLogger(TissueExceptionHandler.class);
		logger.error(module+" "+ message + " Exception message(" + e.getLocalizedMessage() + ")",e);
		//e.printStackTrace();
	}
}
