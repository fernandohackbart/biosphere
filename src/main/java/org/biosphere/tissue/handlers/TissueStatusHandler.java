package org.biosphere.tissue.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.biosphere.tissue.protocol.TissueStatusResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TissueStatusHandler extends AbstractHandler {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		getLogger().debug("TissueStatusHandler.doPost() Request from: " + partnerCell);
		ObjectMapper mapper = new ObjectMapper();
		TissueStatusResponse tsr = new TissueStatusResponse();
		tsr.setTissueName(getCell().getDna().getTissueName());
		tsr.setTissueSize(getCell().getDna().getTissueSize());
		tsr.setTissueCells(getCell().getDna().getTissueCellsInterfaces());
		String responseString = mapper.writeValueAsString(tsr);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}

}
