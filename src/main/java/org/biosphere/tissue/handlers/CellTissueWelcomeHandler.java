package org.biosphere.tissue.handlers;

import java.io.IOException;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.TissueWelcomeResponse;
import org.biosphere.tissue.protocol.TissueWelcomeRequest;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellTissueWelcomeHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		TissueWelcomeRequest twr = mapper.readValue(requestPayload.getBytes(), TissueWelcomeRequest.class);
		getLogger().info("CellTissueWelcomeHandler.doPost() Welcome request to tissue " + twr.getTissueName() + " from cell (" + twr.getCellName()+") at "+ partnerCell);
		
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
			getLogger().info("CellTissueWelcomeHandler.doPost() Sending greetings to cell:" + twr.getCellName());
		}
		else
		{
			tg.setMessage("Busy");
			getLogger().info("CellTissueWelcomeHandler.doPost() Sending busy to cell (" + twr.getCellName()+")");
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

}
