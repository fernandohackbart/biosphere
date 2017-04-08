package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;

public class CellDNACoreHandler implements CellHTTPHandlerInterface {

	private Logger logger;

	public CellDNACoreHandler() {
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
			String fileName = t.getHttpContext().getPath().substring(1);
			String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("CellDNACoreHandler.run()", "Request for: " + fileName + " from " + partnerCell);
			String dnaCore = null;
			try {
				dnaCore = getCell().getCellDNA().getDNACoreAsString();
			} catch (NullPointerException e) {
				TissueExceptionHandler.handleGenericException(e, "CellDNACoreHandler.handle()",
						"getCell().getCellDNA().getDNACoreAsString() NullPointerException :");
			}
			if (dnaCore != null) {
				Headers h = t.getResponseHeaders();
				h.add("Content-Type", "application/xml");
				t.sendResponseHeaders(200, dnaCore.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(dnaCore.getBytes(), 0, dnaCore.getBytes().length);
				os.close();
				logger.info("CellDNACoreHandler.run()", "Served: " + fileName + " size:" + dnaCore.getBytes().length);
			} else {
				String response = "<h1>404 Not Found</h1> Resource: " + fileName + " not found.\n";
				Headers h = t.getResponseHeaders();
				h.add("Content-Type", "text/html");
				t.sendResponseHeaders(404, response.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes(), 0, response.getBytes().length);
				os.close();
				logger.debug("CellDNACoreHandler.run()", "Resource: " + fileName + " is empty.");
			}
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellDNACoreHandler.handle()", "IOException:");
		}
	}
}
