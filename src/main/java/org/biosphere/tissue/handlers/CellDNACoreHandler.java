package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellDNACoreHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public CellDNACoreHandler() {
		logger = LoggerFactory.getLogger(CellDNACoreHandler.class);
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
	
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	
	private String getContentEncoding()
	{
		return this.contentEncoding;
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String fileName = request.getServletPath().substring(1);
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("CellDNACoreHandler.doPost() Request for: " + fileName + " from " + partnerCell);
		String dnaCore = null;
		try {
			dnaCore = getCell().getCellDNA().getDNACoreAsString();
		} catch (NullPointerException e) {
			TissueExceptionHandler.handleGenericException(e, "CellDNACoreHandler.doPost()",
					"getCell().getCellDNA().getDNACoreAsString() NullPointerException :");
		}
		if (dnaCore != null) {
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentLength(dnaCore.getBytes().length);
			response.getWriter().println(dnaCore);
			response.flushBuffer();
			logger.info("CellDNACoreHandler.doPost() Served: " + fileName + " size:" + dnaCore.getBytes().length);
		} else {
			String responseString = "<h1>404 Not Found</h1> Resource: " + fileName + " not found.\n";
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.debug("CellDNACoreHandler.doPost() Resource: " + fileName + " is empty.");
		}		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
}
