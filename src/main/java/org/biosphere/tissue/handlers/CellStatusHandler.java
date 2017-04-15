package org.biosphere.tissue.handlers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.CellInterface;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.utils.KeystoreManager;
import org.biosphere.tissue.utils.Logger;

public class CellStatusHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;

	public CellStatusHandler() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("CellStatus.doPost()",
				"##############################################################################");
		logger.debug("CellStatus.doPost()", "Cell " + cell.getCellName() + " request from: " + partnerCell);
		
		StringBuffer responseSB = new StringBuffer();
		responseSB.append("##############################################################################\n");		
		responseSB.append("Cell status page for cell " + getCell().getCellName() + "\n");
		responseSB.append("Cell network name: " + getCell().getCellNetworkName() + "\n");
		responseSB.append("Cell tissue port: " + getCell().getTissuePort() + "\n");
		responseSB.append("Cell tissue certificate: \n" + getCell().getCellCertificate());
		responseSB.append("##############################################################################\n");
		responseSB.append("Tissue name: " + getCell().getCellDNA().getTissueName() + "\n");
		responseSB.append("Tissue size: " + getCell().getCellDNA().getTissueSize() + "\n");
		responseSB.append("##############################################################################\n");
		responseSB.append("Tissue cells: \n");
		List<CellInterface> celIterfaces = getCell().getCellDNA().getTissueCellsInterfaces();
		Iterator cellsIfIterator = celIterfaces.iterator();
		while (cellsIfIterator.hasNext()) {
			CellInterface cellInterface = (CellInterface) cellsIfIterator.next();
			responseSB.append("  Tissue cell: " + cellInterface.getCellName() + "("
					+ cellInterface.getCellNetworkName() + ":" + cellInterface.getPort() + ")\n");
		}
		responseSB.append("##############################################################################\n");
		responseSB.append("Chain dump: " + getCell().getChain().dumpChain() + "\n");
		responseSB.append("##############################################################################\n");
		responseSB.append("Keystore dump: \n" + new KeystoreManager().dumpKeystore(getCell().getCellKeystore(),
				getCell().getCellKeystorePWD(), getCell().getCellName()) + "\n");
		responseSB.append("##############################################################################\n");
		// response.append("Keystore algorithms: \n"+new
		// KeystoreManager().showAlgorithm()+"\n");
		responseSB.append("##############################################################################\n");
		// response.append("Tissue DNA:
		// \n"+getCell().getCellDNA().getDNACoreAsPrettyString()+"\n");
		responseSB.append("##############################################################################\n");
		Hashtable<String, String> statusTable = new Hashtable<String, String>();
		statusTable = ServiceManager.getStatus();
		Enumeration serviceList = statusTable.keys();
		while (serviceList.hasMoreElements()) {
			String serviceName = (String) serviceList.nextElement();
			responseSB.append("Service: " + serviceName + " Status: " + statusTable.get(serviceName)+"\n");
			responseSB.append("Service: " + serviceName + " dump: \n" + ServiceManager.getServletStatus(serviceName).toString()+"\n");
			responseSB.append("##############################################################################\n");
		}
		String responseString = responseSB.toString();
		response.setContentType(getContentType());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
