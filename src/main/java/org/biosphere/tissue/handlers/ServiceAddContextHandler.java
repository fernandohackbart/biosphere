package org.biosphere.tissue.handlers;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.services.ServletHandlerDefinition;
import org.biosphere.tissue.utils.RequestUtils;
import org.biosphere.tissue.protocol.ServiceServletContext;
import org.biosphere.tissue.protocol.ServiceServletContextURI;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceAddContextHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		
        ObjectMapper mapper = new ObjectMapper();
        ServiceServletContext ssc = mapper.readValue(requestPayload.getBytes(),ServiceServletContext.class);
        
		ServletHandlerDefinition cellSampleServiceSHD = new ServletHandlerDefinition();
		cellSampleServiceSHD.setClassName(ssc.getClassName());
		cellSampleServiceSHD.setContentType(ssc.getContentType());
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		for (ServiceServletContextURI context : ssc.getContextURIs() )
		{
			chainParseChainContexts.add(context.getContextURI());	
		}
		cellSampleServiceSHD.setContexts(chainParseChainContexts);
		ServiceManager.addServletContext(ssc.getServiceName(),cellSampleServiceSHD, getCell());

		getLogger().debug("ServiceContextManagerHandler.doPost() Request from: " + partnerCell);
		String responseString = "<h1>ServiceContextManagerHandler.doPost()</h> "+ ssc.getClassName()+ " from " + partnerCell;
     	response.setContentType(getContentType());
     	response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);	
		response.flushBuffer();
	}
	
}
