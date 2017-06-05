package org.biosphere.tissue.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.biosphere.tissue.protocol.CellStatusResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CellStatusHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		CellStatusResponse csr = new CellStatusResponse();
		csr.setTissueName(getCell().getDna().getTissueName());
		csr.setTissueSize(getCell().getDna().getTissueSize());
		csr.setCellNetworkName(getCell().getCellNetworkName());
		csr.setCellTissuePort(getCell().getTissuePort());
		Runtime runtime = Runtime.getRuntime();
		csr.setAllocatedMemory(runtime.totalMemory());
		csr.setFreeMemory(runtime.freeMemory());
		csr.setMaxMemory(runtime.maxMemory());
		csr.setCellName(getCell().getCellName());
		String responseString = mapper.writeValueAsString(csr);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}

}
