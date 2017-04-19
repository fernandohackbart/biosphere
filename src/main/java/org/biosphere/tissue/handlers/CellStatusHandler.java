package org.biosphere.tissue.handlers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.KeystoreManager;


public class CellStatusHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		getLogger().debug("CellStatus.doPost() ##############################################################################");
		getLogger().debug("CellStatus.doPost() Cell " + getCell().getCellName() + " request from: " + partnerCell);
		
		StringBuffer responseSB = new StringBuffer();
		responseSB.append("##############################################################################\n");
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		responseSB.append("free memory: " + freeMemory / 1024+"\n");
		responseSB.append("allocated memory: " + allocatedMemory / 1024+"\n");
		responseSB.append("max memory: " + maxMemory / 1024+"\n");
		responseSB.append("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024+"\n");
		responseSB.append("##############################################################################\n");
		responseSB.append("Log level: trace="+getLogger().isTraceEnabled()+" debug="+getLogger().isDebugEnabled()+" info="+getLogger().isInfoEnabled()+" warn="+getLogger().isWarnEnabled()+" error="+getLogger().isErrorEnabled()+"\n");
		responseSB.append("Log level: "+TissueManager.logLevelParameter+"="+System.getProperty(TissueManager.logLevelParameter)+"\n");
		responseSB.append("Log level: "+TissueManager.logOutputParameter+"="+System.getProperty(TissueManager.logOutputParameter)+"\n");
		responseSB.append("Log level: "+TissueManager.logShowDateTimeParameter+"="+System.getProperty(TissueManager.logShowDateTimeParameter)+"\n");
		responseSB.append("##############################################################################\n");		
		responseSB.append("Cell status page for cell " + getCell().getCellName() + "\n");
		responseSB.append("Cell network name: " + getCell().getCellNetworkName() + "\n");
		responseSB.append("Cell tissue port: " + getCell().getTissuePort() + "\n");
		responseSB.append("Cell tissue certificate: \n" + getCell().getCellCertificate());
		responseSB.append("##############################################################################\n");
		responseSB.append("Tissue name JSON: " + getCell().getDna().getTissueName() + "\n");
		responseSB.append("Tissue size JSON: " + getCell().getDna().getTissueSize() + "\n");
		responseSB.append("##############################################################################\n");
		responseSB.append("Tissue cells JSON: \n");
		List<CellInterface> cellInterfaces = getCell().getDna().getTissueCellsInterfaces();
		Iterator<CellInterface> ciitJSON = cellInterfaces.iterator();
		while (ciitJSON.hasNext()) {
			CellInterface cif = ciitJSON.next();
			responseSB.append("  Tissue cell: " + cif.getCellName() + "(" + cif.getCellNetworkName() + ":" + cif.getPort() + ")\n");
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
		responseSB.append("Tissue DNA JSON: \n"+getCell().getDna().toJSON()+"\n");
		responseSB.append("##############################################################################\n");
		Hashtable<String, String> statusTable = new Hashtable<String, String>();
		statusTable = ServiceManager.getStatus();
		Enumeration<String> serviceList = statusTable.keys();
		while (serviceList.hasMoreElements()) {
			String serviceName = serviceList.nextElement();
			responseSB.append("Service: " + serviceName + " Status: " + statusTable.get(serviceName)+"\n");
			//responseSB.append("Service: " + serviceName + " dump: \n" + ServiceManager.getServletStatus(serviceName).toString()+"\n");
			responseSB.append("##############################################################################\n");
		}
		String responseString = responseSB.toString();
		response.setContentType(getContentType());
		response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);
		response.flushBuffer();
	}
	
}
