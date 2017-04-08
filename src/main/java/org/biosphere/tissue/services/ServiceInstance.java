package org.biosphere.tissue.services;

import com.sun.net.httpserver.HttpServer;

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
	private HttpServer httpServer;
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

	public void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	public HttpServer getHttpServer() {
		return httpServer;
	}
}
