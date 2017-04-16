package org.biosphere.tissue.blockchain;

import org.biosphere.tissue.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainExceptionHandler {
	public ChainExceptionHandler() {
		super();
	}

	public static void handleUnrecoverableGenericException(Exception e, String module, String message)
			throws BlockException {
		BlockException blockException = new BlockException(message, module, e);
		e.printStackTrace();
		throw blockException;
	}

	public static void handleGenericException(Exception e, String module, String message) {
		Logger logger = LoggerFactory.getLogger(ChainExceptionHandler.class);
		logger.error(module +" "+ message + " Exception message(" + e.getMessage() + ")",e);
		e.printStackTrace();
	}
}
