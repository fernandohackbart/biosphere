package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class CellTissueWelcomeHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public CellTissueWelcomeHandler() {
		logger = new Logger();
	}

	private Cell cell;

	@Override
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
			logger.debug("CellTissueWelcomeHandler.handle()", "Request from: " + partnerCell);
			String requestPayload = RequestUtils.getRequestAsString(t.getRequestBody());

			if (requestPayload.startsWith("WLCM:")) {
				String requestWelcome = requestPayload.substring(0, requestPayload.indexOf("\n"));
				String requestTissue = requestWelcome.split(":")[1];
				String requestCell = requestWelcome.split(":")[2];
				logger.debug("CellTissueWelcomeHandler.handle()",
						"Welcome request to tissue " + requestTissue + " from cell:" + requestCell);

				String requestCert = requestPayload.substring(requestPayload.indexOf("\n") + 1);
				try {
					CellManager.addCellTrustKeystore(requestCell, requestCert, cell);
				} catch (CertificateEncodingException | KeyStoreException e) {
					TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.handle()",
							"CertificateEncodingException/KeyStoreException:");
				} catch (CertificateException e) {
					TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.handle()",
							"CertificateException:");
				}
			}

			String response = "GRTS:" + getCell().getCellName() + "\n" + cell.getCellCertificate();
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "application/xml");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellDNACoreHandler.handle()", "IOException:");
		}
	}
}
