package org.biosphere.tissue.exceptions;

import org.biosphere.tissue.utils.TissueLogger;

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
		TissueLogger logger = new TissueLogger();
		logger.exception(module, message + " Exception message(" + e.getMessage() + ")");
		e.printStackTrace();
	}
}
