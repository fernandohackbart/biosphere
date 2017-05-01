package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.DNA.DNA;
import org.biosphere.tissue.DNA.Tissue;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.protocol.FlatChain;
import org.biosphere.tissue.protocol.TissueJoinRequest;
import org.biosphere.tissue.protocol.TissueJoinResponse;
import org.biosphere.tissue.utils.RequestUtils;
import org.bouncycastle.util.encoders.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellTissueJoinHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			ObjectMapper mapper = new ObjectMapper();
			TissueJoinResponse tjresp = new TissueJoinResponse();			
			tjresp.setCellName(getCell().getCellName());
			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			TissueJoinRequest tjreq = mapper.readValue(requestPayload.getBytes(), TissueJoinRequest.class);
			getLogger().debug("CellTissueJoinHandler.doPost() Request ("+tjreq.getRequestID()+") from: " + partnerCell);
			
			if (!getCell().isTissueMember()) {

				// Add the DNA to the cell
				getLogger().info("CellTissueJoinHandler.doPost() Creating new DNA for this cell");
				Tissue tissue = mapper.readValue(Base64.decode(tjreq.getDna()), Tissue.class);
				DNA dna = new DNA(tissue);
				getCell().setDna(dna);

				// getLogger().info("CellTissueJoinHandler.doPost() Adding local
				// cell to the local DNA (just in case the adopter forgot)");
				// getCell().getDna().appendCell(getCell().getCellName(),
				// getCell().getCellCertificate(),
				// getCell().getCellNetworkName(),getCell().getTissuePort(),getCell().getCellName(),getCell().getChain());

				// Add the chain to the cell
				getLogger().info("CellTissueJoinHandler.doPost() Parsing the Chain received");
				FlatChain fc = mapper.readValue(Base64.decode(tjreq.getChain()), FlatChain.class);
				Chain tmpChain = new Chain(getCell().getCellName(), getCell(), fc);
				getLogger().trace("ChainParseChainHandler.doPost() Parsed flatChain: \n" + tmpChain.toJSON());
				getCell().setChain(tmpChain);

				getCell().setTissueMember(true);
				
				tjresp.setMessage(getCell().getCellName() + " sucessfull joined the tissue!");
				tjresp.setCellJoinedTissue(true);

			} else {
				tjresp.setMessage(getCell().getCellName() + " already member of tissue ("+getCell().getDna().getTissueName()+")");
				tjresp.setCellJoinedTissue(false);
			}
			tjresp.setRequestID(tjreq.getRequestID());

			String responseString = mapper.writeValueAsString(tjresp);
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentLength(responseString.getBytes().length);
			response.getWriter().println(responseString);
			response.flushBuffer();
			getLogger().info("CellTissueJoinHandler.doPost() Joined tissue : " + getCell().getDna().getTissueName()
					+ " from: " + partnerCell);
		} catch (BlockException e) {
			ChainExceptionHandler.handleGenericException(e, "ChainAddBlockHandler.doPost()", "BlockException:");
		}
	}

}
