package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceEnableRequest extends AbstractProtocol {
	
	@JsonProperty("serviceName")
	String serviceName;
	
	@JsonProperty("enableService")
	boolean enableService;

	@JsonProperty("serviceName")
	public final String getServiceName() {
		return serviceName;
	}

	@JsonProperty("serviceName")
	public final void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@JsonProperty("enableService")
	public final boolean isEnableService() {
		return enableService;
	}

	@JsonProperty("enableService")
	public final void setEnableService(boolean enableService) {
		this.enableService = enableService;
	}

}
