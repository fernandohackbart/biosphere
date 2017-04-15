package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;
import org.biosphere.tissue.services.ServiceManager;

public class ServiceStopHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;

	public ServiceStopHandler() {
		logger = new Logger();
	}

	@Override
	public void setCell(Cell cell) {
		this.cell = cell;
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
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		logger.debug("ServiceStopHandler.doPost()", "Request from: " + partnerCell);
		String responseString = "<h1>ServiceStopHandler.handle()</h1> Cell stop request from: " + partnerCell;
		try {
			ServiceManager.stop("THREAD", requestPayload);
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceStopHandler.handle()",
					"Failed to stop service:");
			responseString = "<h1>ServiceStopHandler.doPost()</h1> Service stop request from: " + partnerCell
					+ " Exception: " + e.getMessage();
		}
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
