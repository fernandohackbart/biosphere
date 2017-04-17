package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.XML.DNAXMLCore;
import org.biosphere.tissue.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		String remoteCell = requestPayload.substring(0, requestPayload.indexOf("\n"));
		//String remoteCertificate = requestPayload.substring(requestPayload.indexOf("\n") + 1);
		logger.info("CellTissueJoinHandler.doPost() Creatting new DNA for this cell");
		DNAXMLCore dna = new DNAXMLCore();
		getCell().setCellXMLDNA(dna);
		logger.info("CellTissueJoinHandler.doPost() DNACore URL received: " + remoteCell);
		dna.load(remoteCell, getCell().getTissuePort());
		logger.info("CellTissueJoinHandler.doPost() Adding local cell to the local DNA (just in case the adopter forgot)");
		dna.addCell(getCell().getCellName(), getCell().getCellCertificate(), getCell().getCellNetworkName(), getCell().getTissuePort());
		getCell().setTissueMember(true);
		String responseString = getCell().getCellName() + " sucessfull joined the tissue!";
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLength(responseString.getBytes().length);
		response.getWriter().println(responseString);	
		response.flushBuffer();
		logger.info("CellTissueJoinHandler.doPost() Joined tissue: " + getCell().getCellXMLDNA().getTissueName() + " from: " + partnerCell);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
