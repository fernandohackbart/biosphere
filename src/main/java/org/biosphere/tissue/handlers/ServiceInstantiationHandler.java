package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceInstantiationHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String clientAddress = request.getRemoteHost() + ":" + request.getRemotePort();
		getLogger().debug("CellSeviceInstantiationHandler.handle() Request from: " + clientAddress);
		String responseString = "<h1>CellSeviceInstantiationHandler.handle()</h1> Hello: " + clientAddress;	
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);	
		response.flushBuffer();
	}

}
