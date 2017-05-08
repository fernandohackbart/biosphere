package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public AbstractHandler() {
		super();
		logger = LoggerFactory.getLogger(AbstractHandler.class);
		//Thread.currentThread().setName(AbstractHandler.class.toString());
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	Cell getCell() {
		return cell;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	String getContentType() {
		return this.contentType;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	String getContentEncoding() {
		return this.contentEncoding;
	}
	
	public final Logger getLogger() {
		return logger;
	}

	public final void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("AbstractHandler.doPost() Abstract handler: (" + getCell() + ")" + partnerCell);
		String responseString = "Abstract handler: " + partnerCell;
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
