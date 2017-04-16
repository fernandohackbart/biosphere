package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.utils.Logger;

public class ChainGetFlatChainHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public ChainGetFlatChainHandler() {
		super();
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
	
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	
	private String getContentEncoding()
	{
		return this.contentEncoding;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("ChainGetFlatChain.doPost()", "Request from: " + partnerCell);
		String responseString = cell.getChain().toJSON();
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
