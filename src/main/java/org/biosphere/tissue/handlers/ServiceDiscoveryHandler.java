package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.protocol.ServiceDiscoveryRequest;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceDiscoveryHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		ServiceDiscoveryRequest sdr = mapper.readValue(requestPayload.getBytes(), ServiceDiscoveryRequest.class);
		ServiceDiscoveryResponse sdrsp = new ServiceDiscoveryResponse();
		getLogger().debug("ServiceDiscoveryHandler.doPost() Request (" + sdr.getRequestID() + ") from cell "
				+ sdr.getRequestingCellName() + " : " + partnerCell);
		if (ServiceManager.isRunning(sdr.getServiceName())) {
			getLogger().debug("ServiceDiscoveryHandler.doPost() Service (" + sdr.getServiceName()
					+ ") is running, returning connection information!");
			sdrsp.setRunning(true);
			sdrsp.setRequestID(sdr.getRequestID());
			sdrsp.setCellName(getCell().getCellName());
			sdrsp.setCellNetworkName(getCell().getCellNetworkName());
			sdrsp.setCellServicePort(
					(int) getCell().getDna().getService(sdr.getServiceName()).getParameterValue("ServiceListenerPort"));
		} else {
			getLogger().debug("ServiceDiscoveryHandler.doPost() Service (" + sdr.getServiceName()
			+ ") is NOT running!");
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
	}

}
