package org.biosphere.tissue.handlers;

import java.io.IOException;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class CellTissueWelcomeHandler extends HttpServlet implements CellJettyHandlerInterface {
	
	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;

	public CellTissueWelcomeHandler() {
		logger = new Logger();
	}
	
	@Override
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	private Cell getCell() {
		return cell;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String getContentType()
	{
		return this.contentType;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("CellTissueWelcomeHandler.doPost()", "Request from: " + partnerCell);
		
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		
		if (requestPayload.startsWith("WLCM:")) {
			String requestWelcome = requestPayload.substring(0, requestPayload.indexOf("\n"));
			String requestTissue = requestWelcome.split(":")[1];
			String requestCell = requestWelcome.split(":")[2];
			logger.debug("CellTissueWelcomeHandler.doPost()",
					"Welcome request to tissue " + requestTissue + " from cell:" + requestCell);

			String requestCert = requestPayload.substring(requestPayload.indexOf("\n") + 1);
			try {
				CellManager.addCellTrustKeystore(requestCell, requestCert, getCell());
			} catch (CertificateEncodingException | KeyStoreException e) {
				TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
						"CertificateEncodingException/KeyStoreException:");
			} catch (CertificateException e) {
				TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
						"CertificateException:");
			}
		}
		String responseString = "GRTS:" + getCell().getCellName() + "\n" + getCell().getCellCertificate();	
		response.setContentType(getContentType());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);	
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
