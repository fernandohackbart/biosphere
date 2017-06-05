package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceStatusItem {

	public ServiceStatusItem() {
	}
	
	@JsonProperty("serviceName")
	String serviceName;
	@JsonProperty("serviceType")
	String serviceType;
	@JsonProperty("serviceStatus")
	String serviceStatus;
	@JsonProperty("serviceClass")
	String serviceClass;
	@JsonProperty("serviceThreadName")
	String serviceThreadName;
	@JsonProperty("serviceThreadState")
	String serviceThreadState;
	@JsonProperty("serviceThreadDaemon")
	boolean serviceThreadDaemon;
	@JsonProperty("serviceURIs")
	String serviceURIs;


	@JsonProperty("serviceName")
	public final String getServiceName() {
		return serviceName;
	}

	@JsonProperty("serviceName")
	public final void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@JsonProperty("serviceStatus")
	public final String getServiceStatus() {
		return serviceStatus;
	}

	@JsonProperty("serviceStatus")
	public final void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	@JsonProperty("serviceClass")
	public final String getServiceClass() {
		return serviceClass;
	}

	@JsonProperty("serviceClass")
	public final void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	@JsonProperty("serviceThreadName")
	public final String getServiceThreadName() {
		return serviceThreadName;
	}

	@JsonProperty("serviceThreadName")
	public final void setServiceThreadName(String serviceThreadName) {
		this.serviceThreadName = serviceThreadName;
	}

	@JsonProperty("serviceThreadState")
	public final String getServiceThreadState() {
		return serviceThreadState;
	}

	@JsonProperty("serviceThreadState")
	public final void setServiceThreadState(String serviceThreadState) {
		this.serviceThreadState = serviceThreadState;
	}

	@JsonProperty("serviceThreadDaemon")
	public final boolean getServiceThreadDaemon() {
		return serviceThreadDaemon;
	}

	@JsonProperty("serviceThreadDaemon")
	public final void setServiceThreadDaemon(boolean serviceThreadDaemon) {
		this.serviceThreadDaemon = serviceThreadDaemon;
	}

	@JsonProperty("serviceURIs")
	public final String getServiceURIs() {
		return serviceURIs;
	}

	@JsonProperty("serviceURIs")
	public final void setServiceURIs(String serviceURIs) {
		this.serviceURIs = serviceURIs;
	}
	
	@JsonProperty("serviceType")
	public final String getServiceType() {
		return serviceType;
	}
	
	@JsonProperty("serviceType")
	public final void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
}
