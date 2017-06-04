package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.ServiceDiscoveryRequest;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceDiscoveryHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			ObjectMapper mapper = new ObjectMapper();
			ServiceDiscoveryRequest sdr = mapper.readValue(requestPayload.getBytes(), ServiceDiscoveryRequest.class);
			ServiceDiscoveryResponse sdrsp = new ServiceDiscoveryResponse();
			getLogger().debug("ServiceDiscoveryHandler.doPost() Request (" + sdr.getRequestID() + ") from cell " + sdr.getRequestingCellName() + " : " + partnerCell);
			if (ServiceManager.isRunning(sdr.getServiceName())) {
				getLogger().debug("ServiceDiscoveryHandler.doPost() Service (" + sdr.getServiceName() + ") is running, returning connection information!");
				sdrsp.setRunning(true);
				sdrsp.setRequestID(sdr.getRequestID());
				sdrsp.setCellName(getCell().getCellName());
				sdrsp.setCellNetworkName(getCell().getCellNetworkName());
				sdrsp.setCellServicePort((int) getCell().getDna().getService(sdr.getServiceName()).getParameterValue(TissueManager.TissueServicePortParameter));
			} else {
				getLogger().debug("ServiceDiscoveryHandler.doPost() Service (" + sdr.getServiceName() + ") is NOT running!");
				sdrsp.setCellName(getCell().getCellName());
				sdrsp.setRunning(false);
			}
			String responseString = mapper.writeValueAsString(sdrsp);
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(responseString);
			response.flushBuffer();
			
		} catch (Exception e) {
			return500(request.getRequestURI(), response);
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoveryHandler.handle()", "exception occured:");
		}
	}

	private void return500(String contextPath, HttpServletResponse response) {
		try {
			getLogger().debug("ServiceDiscoveryHandler.return500 contextPath(" + contextPath + ")  HTTP-500");
			String responseString = "<h1>ServiceDiscoveryHandler</h1> HTTP-500 ContextPath: " + contextPath + "!";
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(responseString);
			response.flushBuffer();

		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceNotFoundHandler.return505", "Failed to respond to request!");
		}
	}
}
