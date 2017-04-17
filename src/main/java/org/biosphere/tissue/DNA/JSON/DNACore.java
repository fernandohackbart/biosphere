package org.biosphere.tissue.DNA.JSON;

import java.util.ArrayList;
import org.biosphere.tissue.services.ServiceDefinition;

public class DNACore {
	
	public void incept() {

	}

	public String getTissueName() {
		String response = "ERROR: TissueName not found!";
		return response;
	}

	private void setTissueName(String tissueName) {
	}
	
	public int getTissueSize() {
		return 0;
	}
	
	public void addCell(String cellName, String cellpublickey, String cellNetworkName, int cellTissuePort) {
		
	}
	
	public ArrayList<Cell> getCells() {
		return new ArrayList<Cell>();
	}
	
	public void addService(ServiceDefinition serviceDefinition) {
		
	}

	public ArrayList<Service> getServices() {
		return new ArrayList<Service>();
	}
	
	private boolean serviceAdded(String serviceName) {
		return true;
	}
	
	public ServiceDefinition getServiceDefinition(String serviceName) {
		return new ServiceDefinition();
	}
	public void persist(String fileName) {
	}
	
	public void load(String DNACoreURL, int DNAContentServerPort) {
	}

}
