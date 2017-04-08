package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;

public class CellSampleServiceHandler implements CellHTTPHandlerInterface {
	public CellSampleServiceHandler() {
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
			String clientAddress = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("CellSampleServiceHandler.handle()", "Request from: " + clientAddress);
			String response = "<h1>CellSampleServiceHandler.handle()</h1> Hello: " + clientAddress;
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/html");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellSampleServiceHandler.handle()", "IOException:");
		}
	}
}
