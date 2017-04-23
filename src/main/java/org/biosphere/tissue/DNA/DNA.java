package org.biosphere.tissue.DNA;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.BlockAddRequest;
import org.biosphere.tissue.protocol.BlockAddResponse;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.TissueAddCellPayload;
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
	}

	public String getTissueName() {
		return tissue.getName();
	}

	public int getTissueSize() {
		return tissue.getCells().size();
	}

	private boolean containsCell(String cellName) {
		boolean cellPresent = false;
		logger.debug("DNACore.contains() Getting the list of the cells from the DNA");
		for (Cell cell : tissue.getCells()) {
			if (cell.getName().equals(cellName)) {
				cellPresent = true;
				break;
			}
		}
		return cellPresent;
	}

	public boolean addCell(String cellName, String cellpublickey, String cellNetworkName, int cellTissuePort,String adopterCellName,Chain chain) throws BlockException, JsonProcessingException {
		boolean cellAdded=false;
		logger.info("DNA.addCell() Notifying tissue over chain for cell " + cellName);
		Cell cell = getCellInstance(cellName, cellpublickey, cellNetworkName, cellTissuePort);
		TissueAddCellPayload tacp = new TissueAddCellPayload();
		tacp.setAdopterCellName(adopterCellName);
		tacp.setCell(cell);
		tacp.setOperation("CellAdd");
		ObjectMapper mapper = new ObjectMapper();
		BlockAddRequest bar = new BlockAddRequest();
		bar.setEnsureAcceptance(true);
		bar.setPayload(Base64.toBase64String(mapper.writeValueAsString(tacp).getBytes()));
		//TODO check why the new cell is being notified 
		BlockAddResponse baresp = chain.addBlock(bar);
		if (baresp.isAccepted())
		{
			logger.info("DNA.addCell() Cell " + cellName+ " accepted in the Tissue with block "+baresp.getBlockID());
			logger.info("DNA.addCell() Adding cell " + cellName+ " to the local DNA!");
			appendCell(cell);	
			cellAdded=true;
		}
		else
		{
			logger.warn("DNA.addCell() Tissue not accepted cell " + cellName + " block ("+baresp.getBlockID()+")");
		}
		return cellAdded;
	}

	public void appendCell(Cell cell) {
		if (!containsCell(cell.getName())) {
			logger.info("DNA.appendCell() Cell " + cell.getName() + " being appended to the DNA!");
			tissue.getCells().add(cell);
		} else {
			logger.warn("DNA.appendCell() Cell " + cell.getName() + " already present in the DNA!");
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
