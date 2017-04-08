package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.ChainException;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class ChainAppendBlockHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public ChainAppendBlockHandler() {
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
			logger.debugAppBlock("ChainAppendBlockHandler.handle()",
					"##############################################################################");
			logger.debugAppBlock("ChainAppendBlockHandler.handle()",
					"Cell " + cell.getCellName() + " request from: " + partnerCell);
			String request = RequestUtils.getRequestAsString(t.getRequestBody());
			boolean accepted = cell.getChain().appendBlock(request);
			logger.debugAppBlock("ChainAppendBlockHandler.handle()",
					"Block accepted by " + cell.getCellName() + ":" + accepted);
			String response = getCell().getCellName() + ":" + accepted;

			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "application/xml");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
			logger.debugAppBlock("ChainAppendBlockHandler.handle()", "Response: " + response);
		} catch (IOException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAppendBlockHandler.handle()", "IOException:");
		} catch (ChainException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAppendBlockHandler.handle()", "Exception:");
		} catch (Exception e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAppendBlockHandler.handle()", "Exception:");
		}
	}
}
