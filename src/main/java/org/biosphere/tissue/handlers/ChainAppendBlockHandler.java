package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.blockchain.ChainException;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.protocol.BlockAppendRequest;
import org.biosphere.tissue.protocol.BlockAppendResponse;
import org.biosphere.tissue.utils.RequestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainAppendBlockHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());

			ObjectMapper mapper = new ObjectMapper();
			BlockAppendRequest fbar = mapper.readValue(requestPayload.getBytes(), BlockAppendRequest.class);
			getLogger().debug("ChainAppendBlockHandler.doPost() Request received from: cell (" + fbar.getNotifyingCell() + ") " + partnerCell);
			getLogger().trace("ChainAppendBlockHandler.doPost() Appending block using payload: " + requestPayload);

			boolean accepted = false;
			if (getCell().getChain() instanceof Chain) {
				getLogger().debug("ChainAppendBlockHandler.doPost() Appending block (" + fbar.getBlockID() +") TITLE("+fbar.getTitle()+")");
				accepted = getCell().getChain().appendBlock(fbar);
				getLogger().debug("ChainAppendBlockHandler.doPost() Block accepted by " + getCell().getCellName() + ":"
						+ accepted);
			} else {
				getLogger().debug("ChainAppendBlockHandler.doPost() Chain is empty, ignoring request");
			}

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
			getLogger().debug("ChainAppendBlockHandler.doPost() Response: " + responseString);
		} catch (ChainException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAppendBlockHandler.doPost()", "ChainException:");
		} catch (Exception e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAppendBlockHandler.doPost()", "Exception:");
		}

	}

}
