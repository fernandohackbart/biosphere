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
import org.biosphere.tissue.protocol.TissueWelcomeResponse;
import org.biosphere.tissue.protocol.TissueWelcomeRequest;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellTissueWelcomeHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public CellTissueWelcomeHandler() {
		logger = LoggerFactory.getLogger(CellTissueWelcomeHandler.class);
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
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		TissueWelcomeRequest twr = mapper.readValue(requestPayload.getBytes(), TissueWelcomeRequest.class);
		logger.info("CellTissueWelcomeHandler.doPost() Welcome request to tissue " + twr.getTissueName() + " from cell (" + twr.getCellName()+") at "+ partnerCell);
		
		TissueWelcomeResponse tg = new TissueWelcomeResponse();
		if ((!TissueManager.isOnWelcomeProcess())&&(!getCell().isTissueMember()))
		{
			TissueManager.setOnWelcomeProcess(true);
			try {
				CellManager.addCellTrustKeystore(twr.getCellName(), twr.getCellCertificate(), getCell());
			} catch (CertificateEncodingException | KeyStoreException e) {
				TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
						"CertificateEncodingException/KeyStoreException:");
			} catch (CertificateException e) {
				TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
						"CertificateException:");
			}			
			tg.setMessage("Greetings");
			logger.info("CellTissueWelcomeHandler.doPost() Sending greetings to cell:" + twr.getCellName());
		}
		else
		{
			tg.setMessage("Busy");
			logger.info("CellTissueWelcomeHandler.doPost() Sending busy to cell:" + twr.getCellName());
		}
		tg.setCellName(getCell().getCellName());
		String responseString = mapper.writeValueAsString(tg);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);
		response.flushBuffer();
		TissueManager.setOnWelcomeProcess(false);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
