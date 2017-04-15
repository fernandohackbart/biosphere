package org.biosphere.tissue.handlers;

import java.io.IOException;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.services.ServletHandlerDefinition;
import org.biosphere.tissue.utils.Logger;

public class CellHTTPContextManagerHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	
	public CellHTTPContextManagerHandler() {
		logger = new Logger();
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
	private Cell getCell() {
		return cell;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String getContentType()
	{
		return this.contentType;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String serviceName = "CellServiceListener";
		ServletHandlerDefinition cellSampleServiceSHD = new ServletHandlerDefinition();
		cellSampleServiceSHD.setClassName("org.biosphere.tissue.handlers.CellSampleServiceHandler");
		cellSampleServiceSHD.setContentType("text/html");
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		chainParseChainContexts.add("/org/biosphere/tissue/CellSampleService");
		cellSampleServiceSHD.setContexts(chainParseChainContexts);
		ServiceManager.addServletContext(serviceName,cellSampleServiceSHD, cell);
		logger.debug("CellHTTPContextAdd.doPost()", "Request from: " + partnerCell);
		String responseString = "<h1>CellHTTPContextAdd.doPost()</h1> This should be replaced by the Jetty way if working!!!" + partnerCell;
     	response.setContentType(getContentType());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
