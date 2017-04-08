package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.DNACore;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class CellTissueJoinHandler implements CellHTTPHandlerInterface {
	private Logger logger;

	public CellTissueJoinHandler() {
		logger = new Logger();
	}

	private Cell cell;

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	private Cell getCell() {
		return cell;
	}

	@Override
	public void handle(HttpExchange t) {
		try {
			String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
			logger.debug("CellTissueJoinHandler.handle()", "Request from: " + partnerCell);
			String request = RequestUtils.getRequestAsString(t.getRequestBody());
			String remoteCell = request.substring(0, request.indexOf("\n"));
			String remoteCertificate = request.substring(request.indexOf("\n") + 1);
			logger.info("CellTissueJoinHandler.handle()", "Creatting new DNA for this cell");
			DNACore dna = new DNACore();
			cell.setCellDNA(dna);
			logger.info("CellTissueJoinHandler.handle()", "DNACore URL received: " + remoteCell);
			dna.load(remoteCell, getCell().getTissuePort());
			logger.info("CellTissueJoinHandler.handle()",
					"Adding local cell to the local DNA (just in case the adopter forgot");
			dna.addCell(cell.getCellName(), cell.getCellCertificate(), cell.getCellNetworkName(), cell.getTissuePort());
			getCell().setTissueMember(true);
			String response = getCell().getCellName() + " sucessfull joined the tissue!";

			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "application/xml");
			t.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes(), 0, response.getBytes().length);
			os.close();
			logger.info("CellTissueJoinHandler.handle()",
					"Joined tissue: " + getCell().getCellDNA().getTissueName() + " from: " + partnerCell);
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueJoinHandler.handle()", "IOException:");
		}
		// catch (CertificateEncodingException | KeyStoreException e)
		// {
		// ExceptionHandler.handleGenericException(e,"CellTissueJoinHandler.handle()","CertificateEncodingException
		// | KeyStoreException:");
		// }
		// catch (CertificateException e)
		// {
		// ExceptionHandler.handleGenericException(e,"CellTissueJoinHandler.handle()","CertificateException:");
		// }
	}
}
