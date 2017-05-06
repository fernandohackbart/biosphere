package org.biosphere.tissue.DNA;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.BlockAddRequest;
import org.biosphere.tissue.protocol.BlockAddResponse;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.TissueAddCellPayload;
import org.biosphere.tissue.protocol.TissueRemoveCellPayload;
import org.biosphere.tissue.services.ServiceDefinition;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DNA {

	private Tissue tissue;
	private Logger logger;

	public DNA() {
		super();
		logger = LoggerFactory.getLogger(DNA.class);
		tissue = new Tissue();
		tissue.setDnaVersion("1.0");
		tissue.setName(TissueManager.generateTissueName());
		tissue.setDefaultListenerPort(TissueManager.defaultTissuePort);
		tissue.setDefaultMulticastPort(TissueManager.announcePort);
		tissue.setDefaultMulticastAddress(TissueManager.announceAddress);
	}

	public DNA(Tissue tissue) {
		super();
		logger = LoggerFactory.getLogger(DNA.class);
		this.tissue = tissue;
		//TODO ensure each added cell have its public key in the keystore
	}

	public String getTissueName() {
		return tissue.getName();
	}

	public int getTissueSize() {
		return tissue.getCells().size();
	}

	public boolean containsCell(String cellName) {
		boolean cellPresent = false;
		for (Cell cell : tissue.getCells()) {
			if (cell.getName().equals(cellName)) {
				cellPresent = true;
				break;
			}
		}
		logger.trace("DNACore.contains() Cell ("+cellName+") present = "+cellPresent+" in the DNA!");
		return cellPresent;
	}

	public boolean addCell(String cellName, String cellpublickey, String cellNetworkName, int cellTissuePort,String adopterCellName,org.biosphere.tissue.Cell localCell) throws BlockException, JsonProcessingException {
		boolean cellAdded=false;
		logger.info("DNA.addCell() Notifying tissue over chain for cell (" + cellName+")");
		Cell cell = getCellInstance(cellName, cellpublickey, cellNetworkName, cellTissuePort);
		TissueAddCellPayload tacp = new TissueAddCellPayload();
		tacp.setAdopterCellName(adopterCellName);
		tacp.setCell(cell);
		tacp.setOperation(TissueManager.TissueCellAddOperation);
		ObjectMapper mapper = new ObjectMapper();
		BlockAddRequest bar = new BlockAddRequest();
		bar.setEnsureAcceptance(true);
		bar.setTitle(TissueManager.TissueCellAddOperation+"-"+cellName);
		bar.setPayload(Base64.toBase64String(mapper.writeValueAsString(tacp).getBytes()));
		//TODO check why the new cell is being notified 
		BlockAddResponse baresp = localCell.getChain().addBlock(bar);
		if (baresp.isAccepted())
		{
			logger.info("DNA.addCell() Cell (" + cellName+ ") accepted in the Tissue with block "+baresp.getBlockID());
			logger.info("DNA.addCell() Adding cell (" + cellName+ ") to the local DNA!");
			appendCell(cell,localCell);	
			cellAdded=true;
		}
		else
		{
			logger.warn("DNA.addCell() Tissue not accepted cell " + cellName + " block ("+baresp.getBlockID()+")");
		}
		return cellAdded;
	}

	public void appendCell(org.biosphere.tissue.DNA.Cell cell,org.biosphere.tissue.Cell localCell) {
		if (!containsCell(cell.getName())) {
			logger.info("DNA.appendCell() Cell " + cell.getName() + " being appended to the DNA!");	
			tissue.getCells().add(cell);
			
		} else {
			logger.warn("DNA.appendCell() Cell " + cell.getName() + " already present in the DNA!");
		}
		logger.info("DNA.appendCell() Adding cell (" + cell.getName() + ") public key to the keystore!");
		try {
			CellManager.addCellTrustKeystore(cell.getName(), cell.getPublicKey(),localCell);
		} catch (CertificateEncodingException | KeyStoreException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
					"CertificateEncodingException/KeyStoreException:");
		} catch (CertificateException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
					"CertificateException:");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellTissueWelcomeHandler.doPost()",
					"IOException:");
		}
	}

	public boolean removeCell(String cellName, String cellCertificate, String cellNetworkName, int cellTissuePort,Chain chain) throws BlockException, JsonProcessingException{
		boolean cellRemoved=false;
		Cell cell = getCellInstance(cellName, cellCertificate, cellNetworkName, cellTissuePort);
		logger.info("DNA.removeCell() Notifying tissue over chain for cell (" + cell.getName() +") removal.");
		TissueRemoveCellPayload trcp = new TissueRemoveCellPayload();
		trcp.setRequesterCellName(cell.getName());
		trcp.setToRemoveCell(cell);
		trcp.setOperation(TissueManager.TissueCellRemoveOperation);
		ObjectMapper mapper = new ObjectMapper();
		BlockAddRequest bar = new BlockAddRequest();
		bar.setEnsureAcceptance(true);
		bar.setTitle(TissueManager.TissueCellRemoveOperation+"-"+cell.getName());
		bar.setPayload(Base64.toBase64String(mapper.writeValueAsString(trcp).getBytes()));
		//TODO check why the new cell is being notified 
		BlockAddResponse baresp = chain.addBlock(bar);
		if (baresp.isAccepted())
		{
			logger.info("DNA.removeCell() Cell (" + cell.getName() + ") removed from the Tissue with block "+baresp.getBlockID());
			logger.info("DNA.removeCell() Removing cell (" + cell.getName() + ") from the local DNA!");
			deleteCell(cell);	
			cellRemoved=true;
		}
		else
		{
			logger.warn("DNA.removeCell() Tissue not accepted cell " + cell.getName() + " removal, block ("+baresp.getBlockID()+")");
		}
		return cellRemoved;
	}
	
	public void deleteCell(Cell cell){
		if (containsCell(cell.getName())) {
			logger.info("DNA.deleteCell() Cell " + cell.getName() + " being removed from the DNA!");
			tissue.getCells().remove(cell);
		} else {
			logger.warn("DNA.deleteCell() Cell " + cell.getName() + " not present in the DNA!");
		}
	}
	
	public Cell getCellInstance(String cellName, String cellpublickey, ArrayList<CellNetworkInterface> interfaces, int cellTissuePort) {
		Cell cell = new Cell();
		cell.setName(cellName);
		cell.setPublicKey(cellpublickey);
		cell.setTissuePort(cellTissuePort);
		cell.setInterfaces(interfaces);
		return cell;
	}
	
	public Cell getCellInstance(String cellName, String cellpublickey, String cellNetworkName, int cellTissuePort) {
		Cell cell = new Cell();
		cell.setName(cellName);
		cell.setPublicKey(cellpublickey);
		cell.setTissuePort(cellTissuePort);
		CellNetworkInterface cni = new CellNetworkInterface();
		cni.setHostname(cellNetworkName);
		cell.getInterfaces().add(cni);
		return cell;
	}

	public List<CellInterface> getTissueCellsInterfaces() {
		List<CellInterface> interfacesList = new ArrayList<CellInterface>();
		logger.trace("DNA.getTissueCellsInterfaces() Getting the list of the cells from the DNA");
		for (Cell cell : tissue.getCells()) {
			for (CellNetworkInterface cni : cell.getInterfaces()) {
				interfacesList.add(new CellInterface(cell.getName(), cni.getHostname(), cell.getTissuePort()));
			}
		}
		return interfacesList;
	}

	public ArrayList<Cell> getCells() {
		return tissue.getCells();
	}

	public ArrayList<ServiceDefinition> getServices() {
		ArrayList<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
		for (Service service : tissue.getServices()) {
			services.add(getService(service.getName()));
		}
		return services;
	}

	private boolean isServiceAdded(String serviceName) {
		boolean loaded = false;
		for (Service service : tissue.getServices()) {
			if (service.getName().equals(serviceName)) {
				loaded = true;
				break;
			}
		}
		return loaded;
	}

	public void addService(ServiceDefinition sd) throws IOException {
		if (!isServiceAdded(sd.getName())) {
			logger.info("DNA.addService() Adding service " + sd.getName());
			Service service = new Service();
			service.setName(sd.getName());
			service.setClassName(sd.getClassName());
			service.setDaemon(sd.isDaemon());
			service.setType(sd.getType());
			service.setVersion(sd.getVersion());
			Enumeration<String> pne = sd.getParameters().keys();
			while (pne.hasMoreElements()) {
				ServiceParameter sp = new ServiceParameter();
				String pn = pne.nextElement();
				sp.setName(pn);
				sp.setObjectValue(sd.getParameters().get(pn));
				service.getParameters().add(sp);
			}
			tissue.getServices().add(service);
		} else {
			logger.warn("DNA.addService() Service " + sd.getName() + " already present in the DNA!");
		}
	}

	public ServiceDefinition getService(String serviceName) {
		ServiceDefinition sd = null;
		for (Service service : tissue.getServices()) {
			if (service.getName().equals(serviceName)) {
				sd = new ServiceDefinition();
				sd.setClassName(service.getClassName());
				sd.setDaemon(service.isDaemon());
				sd.setName(service.getName());
				sd.setType(service.getType());
				sd.setVersion(service.getVersion());
				Hashtable<String, Object> serviceDefinitionParameters = new Hashtable<String, Object>();
				for (ServiceParameter sp : service.getParameters()) {
					serviceDefinitionParameters.put(sp.getName(), sp.getObjectValue());
				}
				sd.setParameters(serviceDefinitionParameters);
				break;
			}
		}
		return sd;
	}

	public void fromJSON(String remoteDNAURL) {
		try {
			logger.info("DNA.fromJSON() Loading DNA from remote JSON " + remoteDNAURL);
			URL urlDocument = new URL(remoteDNAURL);
			HttpsURLConnection httpConnectionDocument = (HttpsURLConnection) urlDocument.openConnection();
			httpConnectionDocument.connect();
			String payload = RequestUtils.getRequestAsString(httpConnectionDocument.getInputStream());
			ObjectMapper mapper = new ObjectMapper();
			tissue = mapper.readValue(payload.getBytes(), Tissue.class);
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "DNA.load()", "IOException:");
		}
	}

	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(tissue);
	}
}
