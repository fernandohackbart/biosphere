package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.ChainException;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.protocol.BlockAppendRequest;
import org.biosphere.tissue.protocol.BlockAppendResponse;
import org.biosphere.tissue.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainAppendBlockHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public ChainAppendBlockHandler() {
		super();
		logger = LoggerFactory.getLogger(ChainAppendBlockHandler.class);
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
		try {
			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			logger.debug("ChainAppendBlockHandler.doPost() ##############################################################################");
			logger.debug("ChainAppendBlockHandler.doPost() Cell " + cell.getCellName() + " request from: " + partnerCell);
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			
			ObjectMapper mapper = new ObjectMapper();
			BlockAppendRequest fbar = mapper.readValue(requestPayload.getBytes(),BlockAppendRequest.class);
			
			boolean accepted = cell.getChain().appendBlock(fbar);
			logger.debug("ChainAppendBlockHandler.doPost() Block accepted by " + cell.getCellName() + ":" + accepted);
			
			BlockAppendResponse fbr = new BlockAppendResponse();
			fbr.setAccepted(accepted);
			fbr.setCellName(getCell().getCellName());
			String responseString = mapper.writeValueAsString(fbr);
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.debug("ChainAppendBlockHandler.doPost() Response: " + responseString);
		} catch (ChainException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAddBlockHandler.doPost()", "ChainException:");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
