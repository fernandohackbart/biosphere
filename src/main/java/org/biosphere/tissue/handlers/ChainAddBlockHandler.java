package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.protocol.BlockAddRequest;
import org.biosphere.tissue.protocol.BlockAddResponse;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainAddBlockHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public ChainAddBlockHandler() {
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
		try {
			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			logger.debugAddBlock("ChainAddBlockHandler.doPost()",
					"##############################################################################");
			logger.debugAddBlock("ChainAddBlockHandler.doPost()",
					"Cell " + cell.getCellName() + " request from: " + partnerCell);
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			logger.debugAddBlock("ChainAddBlockHandler.doPost()", "Payload to be added to the block:" + requestPayload);
			ObjectMapper mapper = new ObjectMapper();
			BlockAddRequest bare = mapper.readValue(requestPayload.getBytes(),BlockAddRequest.class);
			BlockAddResponse bar = cell.getChain().addBlock(bare);
			String responseString = mapper.writeValueAsString(bar);
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.debugAddBlock("ChainAddBlockHandler.doPost()", responseString);
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
