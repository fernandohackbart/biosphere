package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDiscoveryRequest extends AbstractProtocol{
	
	@JsonProperty("serviceName")
	String serviceName;
	
	@JsonProperty("requestingCellName")
	String requestingCellName;
	
	@JsonProperty("serviceName")
	public final String getServiceName() {
		return serviceName;
	}
	
	@JsonProperty("serviceName")
	public final void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	@JsonProperty("requestingCellName")
	public final String getRequestingCellName() {
		return requestingCellName;
	}
	
	@JsonProperty("requestingCellName")
	public final void setRequestingCellName(String requestingCellName) {
		this.requestingCellName = requestingCellName;
	}
	

}
