package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.services.ServiceDefinition;
import org.biosphere.tissue.utils.Logger;

public class CellSampleServiceHandler extends HttpServlet implements CellJettyHandlerInterface {
	
	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	
	public CellSampleServiceHandler() {
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
		String responseString="<h1>Hello from HelloServlet from :"+getCell().getCellName()+"</h1>";
		response.setContentType(getContentType());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		logger.debug("CellSampleServiceHandler.doPost()",
				"##############################################################################");
		response.getWriter().println(responseString);		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
