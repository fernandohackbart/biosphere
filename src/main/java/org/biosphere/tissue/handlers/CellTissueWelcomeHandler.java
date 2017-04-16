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
import org.biosphere.tissue.protocol.TissueGreeting;
import org.biosphere.tissue.protocol.TissueWelcome;
import org.biosphere.tissue.utils.TissueLogger;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellTissueWelcomeHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private TissueLogger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public CellTissueWelcomeHandler() {
		logger = new TissueLogger();
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

	private String getContentType() {
		return this.contentType;
	}
	
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	
	private String getContentEncoding()
	{
		return this.contentEncoding;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("CellTissueWelcomeHandler.doPost()", "Request from: " + partnerCell);
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		TissueWelcome tw = mapper.readValue(requestPayload.getBytes(), TissueWelcome.class);
		logger.debug("CellTissueWelcomeHandler.doPost()",
				"Welcome request to tissue " + tw.getTissueName() + " from cell:" + tw.getCellName());
		try {
			CellManager.addCellTrustKeystore(tw.getCellName(), tw.getCellCertificate(), getCell());
		} catch (CertificateEncodingException | KeyStoreException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
					"CertificateEncodingException/KeyStoreException:");
		} catch (CertificateException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
					"CertificateException:");
		}
		TissueGreeting tg = new TissueGreeting();
		tg.setMessage("Greetings");
		tg.setCellName(getCell().getCellName());
		String responseString = mapper.writeValueAsString(tg);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
