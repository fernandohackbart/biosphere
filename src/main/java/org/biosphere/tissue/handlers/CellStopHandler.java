package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.cell.CellManager;

public class CellStopHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		
		getLogger().debug("CellStopHandler.doPost() Request from: " + partnerCell);
		String responseString = "<h1>CellStopHandler.doPost()</h1> Cell stop request from: " + partnerCell;
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);	
		response.flushBuffer();
		CellManager.stopCell();
	}
	
}
