package org.biosphere.tissue.services;

import org.biosphere.tissue.DNA.Service;
import org.biosphere.tissue.DNA.ServiceParameter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.io.IOException;
import java.util.ArrayList;

public class ServiceInstance extends Service {
	
	public ServiceInstance() {
		super();
		serviceInstanceParameters = new ArrayList<ServiceParameter>();
	}

	public ServiceInstance(Service serviceDefinition) {
		super();
		serviceInstanceParameters = new ArrayList<ServiceParameter>();
		loadDefinition(serviceDefinition);
	}

	private String cellName;
	private THREADService threadService;
	private Server jettyServer;
	private ContextHandlerCollection jettyContexts;
	private ServerConnector jettyServerConnector;
	private ArrayList<ServiceParameter> serviceInstanceParameters;

	public final void loadDefinition(Service serviceDefinition) {
		this.setName(serviceDefinition.getName());
		this.setType(serviceDefinition.getType());
		this.setDaemon(serviceDefinition.isDaemon());
		this.setEnabled(serviceDefinition.isEnabled());
		this.setClassName(serviceDefinition.getClassName());
		this.setParameters(serviceDefinition.getParameters());
	}
	
	public final boolean containsInstanceParameter(String key) {
		boolean present = false;
		for (ServiceParameter sp : serviceInstanceParameters) {
			if (sp.getName().equals(key)) {
				present = true;
				break;
			}
		}
		return present;
	}

	public final void addServiceInstanceParameter(String key, Object value) throws IOException {
		ServiceParameter sp = new ServiceParameter();
		sp.setName(key);
		sp.setObjectValue(value);
		serviceInstanceParameters.add(sp);
	}

	public final void removeServiceInstanceParameter(String key) {
		for (ServiceParameter sp : serviceInstanceParameters){
			if(sp.getName().equals(key)){
				//TODO remove the entry from the Array
				//value=sp.getObjectValue();
			}
		}
	}

	public final ArrayList<ServiceParameter> getServiceInstanceParameters() {
		return serviceInstanceParameters;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public String getCellName() {
		return cellName;
	}

	public void setThreadService(THREADService threadService) {
		this.threadService = threadService;
	}

	public THREADService getThreadService() {
		return threadService;
	}

	public final Server getJettyServer() {
		return jettyServer;
	}

	public final void setJettyServer(Server jettyServer) {
		this.jettyServer = jettyServer;
	}

	public final ContextHandlerCollection getJettyContexts() {
		return jettyContexts;
	}

	public final void setJettyContexts(ContextHandlerCollection jettyContexts) {
		this.jettyContexts = jettyContexts;
	}

	public final ServerConnector getJettyServerConnector() {
		return jettyServerConnector;
	}

	public final void setJettyServerConnector(ServerConnector jettyServerConnector) {
		this.jettyServerConnector = jettyServerConnector;
	}
}
