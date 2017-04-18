package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellTissueJoinHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;

	public CellTissueJoinHandler() {
		logger = LoggerFactory.getLogger(CellTissueJoinHandler.class);
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

	private String getContentType() {
		return this.contentType;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	private String getContentEncoding() {
		return this.contentEncoding;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
			logger.debug("CellTissueJoinHandler.doPost() Request from: " + partnerCell);

			String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
			ObjectMapper mapper = new ObjectMapper();
			TissueJoinRequest tjreq = mapper.readValue(requestPayload.getBytes(), TissueJoinRequest.class);
			
			//Add the DNA to the cell
			logger.info("CellTissueJoinHandler.doPost() Creating new DNA for this cell");
			Tissue tissue = mapper.readValue(Base64.decode(tjreq.getDna()),Tissue.class);
			DNA dna = new DNA(tissue);
			getCell().setDna(dna);
			
			logger.info("CellTissueJoinHandler.doPost() Adding local cell to the local DNA (just in case the adopter forgot)");
			getCell().getDna().addCell(getCell().getCellName(), getCell().getCellCertificate(), getCell().getCellNetworkName(),getCell().getTissuePort());
			
			//Add the chain to the cell
			logger.info("CellTissueJoinHandler.doPost() Parsing the Chain received");
			FlatChain fc = mapper.readValue(Base64.decode(tjreq.getChain()), FlatChain.class);
			Chain tmpChain = new Chain(getCell().getCellName(), cell, fc);
			logger.debug("ChainParseChainHandler.doPost() Parsed flatChain: \n" + tmpChain.toJSON());
			getCell().setChain(tmpChain);

			getCell().setTissueMember(true);

			TissueJoinResponse tjresp = new TissueJoinResponse();
			tjresp.setCellName(getCell().getCellName());
			tjresp.setMessage(getCell().getCellName() + " sucessfull joined the tissue!");

			String responseString = mapper.writeValueAsString(tjresp);
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentLength(responseString.getBytes().length);
			response.getWriter().println(responseString);
			response.flushBuffer();
			logger.info("CellTissueJoinHandler.doPost() Joined tissue : " + getCell().getDna().getTissueName()+ " from: " + partnerCell);
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
