package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.protocol.FlatChain;
import org.biosphere.tissue.protocol.TissueGreeting;
import org.biosphere.tissue.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainParseChainHandler extends HttpServlet implements CellServletHandlerInterface {
	
	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public ChainParseChainHandler() {
		super();
		logger = LoggerFactory.getLogger(ChainParseChainHandler.class);
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
			logger.debug("ChainParseChainHandler.doPost() equest from: " + partnerCell);
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			String responseString = getCell().getCellName() + " failed to parse chain!";
			ObjectMapper mapper = new ObjectMapper();
			FlatChain fc = mapper.readValue(requestPayload.getBytes(), FlatChain.class);
			Chain tmpChain = new Chain(getCell().getCellName(), cell, fc);
			logger.debug("ChainParseChainHandler.doPost() Parsed flatChain: \n" + tmpChain.toJSON());
			getCell().setChain(tmpChain);
			responseString = "Chain parsed successfully";
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.info("ChainParseChainHandler.doPost()" + responseString);
		} catch (BlockException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAddBlockHandler.doPost()", "BlockException:");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
