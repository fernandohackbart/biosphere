package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class ChainAddBlockHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;

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
			String responseString = getCell().getCellName() + " failed to add block!";
			logger.debugAddBlock("ChainAddBlockHandler.doPost()", "Payload to be added to the block:" + requestPayload);
			if (cell.getChain().addBlock(requestPayload)) {
				logger.debugAddBlock("ChainAddBlockHandler.handle()", "Request: " + requestPayload);
				responseString = getCell().getCellName() + " added block!";
			}
			response.setContentType(getContentType());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(responseString);
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
