package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceSampleHandler extends AbstractHandler {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String responseString="<h1>ServiceSampleHandler cell :"+getCell().getCellName()+"</h1>";
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		getLogger().debug("ServiceSampleHandler.doPost() Request from "+partnerCell);
		response.getWriter().println(responseString);		
		response.flushBuffer();
	}

}
