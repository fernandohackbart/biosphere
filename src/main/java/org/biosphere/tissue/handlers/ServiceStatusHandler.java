package org.biosphere.tissue.handlers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.protocol.ServiceStatusItem;
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
		Hashtable<String, String> statusTable = new Hashtable<String, String>();
		statusTable = ServiceManager.getStatus();
		Enumeration<String> serviceList = statusTable.keys();
		while (serviceList.hasMoreElements()) {
			String serviceName = serviceList.nextElement();
			ssr.addServiceStatusItem(new ServiceStatusItem(serviceName,statusTable.get(serviceName)));
		}
		String responseString = mapper.writeValueAsString(ssr);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}
}
