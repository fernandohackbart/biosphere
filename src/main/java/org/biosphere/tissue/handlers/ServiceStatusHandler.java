package org.biosphere.tissue.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.biosphere.tissue.protocol.ServiceStatusResponse;
import org.biosphere.tissue.services.ServiceManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceStatusHandler extends AbstractHandler {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		getLogger().debug("ServiceStatusHandler.doPost() Request from: " + partnerCell);
		ObjectMapper mapper = new ObjectMapper();
		ServiceStatusResponse ssr = new ServiceStatusResponse();
		ssr.setServicesStatus(ServiceManager.getStatus());
		String responseString = mapper.writeValueAsString(ssr);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}
}
