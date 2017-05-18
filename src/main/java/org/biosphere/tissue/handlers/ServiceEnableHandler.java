package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.ServiceEnableRequest;
import org.biosphere.tissue.protocol.ServiceEnableResponse;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceEnableHandler extends AbstractHandler {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		ServiceEnableRequest ser = mapper.readValue(requestPayload.getBytes(), ServiceEnableRequest.class);
		ServiceEnableResponse sersp = null;

		getLogger().debug("ServiceEnableHandler.doPost() Request (" + ser.getRequestID() + ") from " + partnerCell);

		if (ServiceManager.isServiceDefined(ser.getServiceName(),getCell())) {
			getLogger().debug("ServiceEnableHandler.doPost() Service (" + ser.getServiceName() + ") enabled = ("
					+ ser.isEnableService() + ")!");
			
			try{
				sersp=getCell().getDna().enableService(ser,getCell());
			} catch(BlockException e){
				TissueExceptionHandler.handleGenericException(e, "ServiceEnableHandler.doPost()",
						"BlockException:");
			}
			
		} else {
			getLogger().debug("ServiceEnableHandler.doPost() Service (" + ser.getServiceName() + ") does NOT exists!");
		}
		String responseString = mapper.writeValueAsString(sersp);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}
}
