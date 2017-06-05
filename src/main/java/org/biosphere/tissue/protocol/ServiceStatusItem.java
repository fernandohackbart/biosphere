package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceStatusItem {
	
	public ServiceStatusItem(String serviceName,String serviceStatus){
		setServiceName(serviceName);
		setServiceStatus(serviceStatus);
	}
	
	@JsonProperty("serviceName")
	String serviceName;
	@JsonProperty("serviceStatus")
	String serviceStatus;
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
}
