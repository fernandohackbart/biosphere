package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.DNA;
import org.biosphere.tissue.protocol.TissueDNALocator;
import org.biosphere.tissue.protocol.TissueJoinResponse;
import org.biosphere.tissue.utils.RequestUtils;

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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("CellTissueJoinHandler.doPost() Request from: " + partnerCell);
		
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		ObjectMapper mapper = new ObjectMapper();
		TissueDNALocator tdl= mapper.readValue(requestPayload.getBytes(), TissueDNALocator.class);
		logger.info("CellTissueJoinHandler.doPost() Creating new DNA for this cell");
		DNA dna = new DNA("TemporaryFrom-"+tdl.getJsonDNAURL());
		getCell().setDna(dna);
		dna.fromJSON(tdl.getJsonDNAURL());
		logger.info("CellTissueJoinHandler.doPost() Adding local cell to the local DNA (just in case the adopter forgot)");
		dna.addCell(getCell().getCellName(), getCell().getCellCertificate(), getCell().getCellNetworkName(), getCell().getTissuePort());		
		
		getCell().setTissueMember(true);
		
		TissueJoinResponse tjr = new TissueJoinResponse();
		tjr.setCellName(getCell().getCellName());
		tjr.setMessage(getCell().getCellName() + " sucessfull joined the tissue!");
		
		String responseString = mapper.writeValueAsString(tjr);
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);	
		response.flushBuffer();
		logger.info("CellTissueJoinHandler.doPost() Joined tissue JSON: " + getCell().getDna().getTissueName() + " from: " + partnerCell);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
