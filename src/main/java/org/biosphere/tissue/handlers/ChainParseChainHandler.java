package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class ChainParseChainHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public ChainParseChainHandler() {
		super();
		logger = new Logger();
	}

	private Cell cell;

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	private Cell getCell() {
		return cell;
	}

	@Override
	public void handle(HttpExchange t) {
		try {
			String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("ChainParseChainHandler.handle()", "Request from: " + partnerCell);
			String request = RequestUtils.getRequestAsString(t.getRequestBody());
			String response = getCell().getCellName() + " failed to parse chain!";
			logger.debug("ChainParseChainHandler.handle()", "flatChain: \n" + request);
			Chain tmpChain = new Chain(getCell().getCellName(), cell, request);
			logger.debug("ChainParseChainHandler.handle()", "Parsed flatChain: \n" + tmpChain.toFlat());
			getCell().setChain(tmpChain);
			response = "Chain parsed successfully";
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "application/xml");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
			logger.info("ChainParseChainHandler.handle()", response);
		} catch (IOException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainParseChainHandler.handle()", "IOException:");
		} catch (Exception e) {
			ChainExceptionHandler.handleGenericException(e, "ChainParseChainHandler.handle()", "Exception:");
		}
	}
}
