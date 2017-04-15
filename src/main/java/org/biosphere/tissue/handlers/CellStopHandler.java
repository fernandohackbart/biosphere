package org.biosphere.tissue.handlers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.utils.Logger;


public class CellStopHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	
	public CellStopHandler() {
		logger = new Logger();
	}

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
		
		logger.debug("CellStopHandler.doPost()", "Request from: " + partnerCell);
		String responseString = "<h1>CellStopHandler.doPost()</h1> Cell stop request from: " + partnerCell;
		response.setContentType(getContentType());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);	
		CellManager.stopCell();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
