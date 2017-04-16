package org.biosphere.tissue.handlers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.utils.Logger;

public class CellDNASchemaHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public CellDNASchemaHandler() {
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
		logger.debug("CellDNASchemaHandler.doPost()", "Request for: " + fileName + " from " + partnerCell);
		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		if (is != null) {
			int fileSize = is.available();
			byte[] bytearray = new byte[(int) fileSize];
			BufferedInputStream bis = new BufferedInputStream(is);
			bis.read(bytearray, 0, bytearray.length);
			response.setContentType("application/xml");
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(bytearray.length);
			response.setStatus(HttpServletResponse.SC_OK);
			OutputStream os = response.getOutputStream();
			os.write(bytearray, 0, bytearray.length);
			os.close();
			is.close();
			response.flushBuffer();
			logger.info("CellDNASchemaHandler.doPost()", "Served: " + fileName + " size:" + fileSize);
		} else {
			String responseString = "<h1>404 Not Found</h1> Resource: " + fileName + " not found.";
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.debug("CellDNASchemaHandler.doPost()", "Resource: " + fileName + " not found.");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
