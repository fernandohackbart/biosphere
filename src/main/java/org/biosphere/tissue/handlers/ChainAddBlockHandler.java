package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class ChainAddBlockHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public ChainAddBlockHandler() {
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
			logger.debugAddBlock("ChainAddBlockHandler.handle()",
					"##############################################################################");
			logger.debugAddBlock("ChainAddBlockHandler.handle()",
					"Cell " + cell.getCellName() + " request from: " + partnerCell);
			String request = RequestUtils.getRequestAsString(t.getRequestBody());
			String response = getCell().getCellName() + " failed to add block!";
			logger.debugAddBlock("ChainAddBlockHandler.handle()", "Payload to be added to the block:" + request);
			if (cell.getChain().addBlock(request)) {
				logger.debugAddBlock("ChainAddBlockHandler.handle()", "Request: " + request);
				response = getCell().getCellName() + " added block!";
			}
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "application/xml");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
			logger.debugAddBlock("ChainAddBlockHandler.handle()", response);
		} catch (IOException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAddBlockHandler.handle()", "IOException:");
		} catch (Exception e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAddBlockHandler.handle()", "Exception:");
		}
	}
}
