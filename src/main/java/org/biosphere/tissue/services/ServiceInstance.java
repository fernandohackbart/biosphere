package org.biosphere.tissue.services;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import java.util.Hashtable;

public class ServiceInstance extends ServiceDefinition {
	public ServiceInstance() {
		super();
		serviceInstanceParameters = new Hashtable<String, Object>();
	}

	public ServiceInstance(ServiceDefinition serviceDefinition) {
		super();
		serviceInstanceParameters = new Hashtable<String, Object>();
		loadDefinition(serviceDefinition);
	}

	private String cellName;
	private THREADService threadService;
	private Server jettyServer;
	private ContextHandlerCollection jettyContexts;
	private ServerConnector jettyServerConnector;
	private Hashtable<String, Object> serviceInstanceParameters;

	public final void loadDefinition(ServiceDefinition serviceDefinition) {
		this.setServiceDefinitionName(serviceDefinition.getServiceDefinitionName());
		this.setServiceDefinitionType(serviceDefinition.getServiceDefinitionType());
		this.setServiceDefinitionDaemon(serviceDefinition.isServiceDefinitionDaemon());
		this.setServiceDefinitionClass(serviceDefinition.getServiceDefinitionClass());
		this.setServiceDefinitionParameters(serviceDefinition.getServiceDefinitionParameters());
	}

	public final void addServiceInstanceParameter(String key, Object value) {
		serviceInstanceParameters.put(key, value);
	}

	public final void removeServiceInstanceParameter(String key) {
		serviceInstanceParameters.remove(key);
	}

	public final Hashtable<String, Object> getServiceInstanceParameters() {
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
