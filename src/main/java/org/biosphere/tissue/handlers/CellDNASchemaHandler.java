package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;

public class CellDNASchemaHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public CellDNASchemaHandler() {
		logger = new Logger();
	}

	private Cell cell;

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public void handle(HttpExchange t) {
		try {
			String fileName = t.getHttpContext().getPath().substring(1);
			String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("CellDNASchemaHandler.run()", "Request for: " + fileName + " from " + partnerCell);
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
			if (is != null) {
				Headers h = t.getResponseHeaders();
				h.add("Content-Type", "application/xml");
				int fileSize = is.available();
				byte[] bytearray = new byte[(int) fileSize];
				BufferedInputStream bis = new BufferedInputStream(is);
				bis.read(bytearray, 0, bytearray.length);
				t.sendResponseHeaders(200, fileSize);
				OutputStream os = t.getResponseBody();
				os.write(bytearray, 0, bytearray.length);
				os.close();
				is.close();
				logger.info("CellDNASchemaHandler.run()", "Served: " + fileName + " size:" + fileSize);
			} else {
				String response = "<h1>404 Not Found</h1> Resource: " + fileName + " not found.";
				Headers h = t.getResponseHeaders();
				h.add("Content-Type", "text/html");
				t.sendResponseHeaders(404, response.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes(), 0, response.getBytes().length);
				os.close();
				logger.debug("CellDNASchemaHandler.run()", "Resource: " + fileName + " not found.");
			}
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellDNASchemaHandler.handle()", "IOException:");
		}
	}
}
