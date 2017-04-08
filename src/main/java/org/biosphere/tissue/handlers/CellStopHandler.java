package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;

public class CellStopHandler implements CellHTTPHandlerInterface {
	public CellStopHandler() {
		logger = new Logger();
	}

	private Logger logger;
	private Cell cell;

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public void handle(HttpExchange t) {
		try {
			String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("CellStopHandler.handle()", "Request from: " + partnerCell);
			String response = "<h1>CellStopHandler.handle()</h1> Cell stop request from: " + partnerCell;
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/html");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
			CellManager.stopCell();
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellStopHandler.handle()", "IOException:");
		}
	}
}
