package org.biosphere.tissue.protocol;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceStatusResponse extends AbstractProtocol {

	@JsonProperty("servicesStatus")
	List<ServiceStatusItem> servicesStatus = new ArrayList<ServiceStatusItem>();

	@JsonProperty("servicesStatus")
	public final List<ServiceStatusItem> getServicesStatus() {
		return servicesStatus;
	}

	@JsonProperty("servicesStatus")
	public final void setServicesStatus(List<ServiceStatusItem> servicesStatus) {
		this.servicesStatus = servicesStatus;
	}
	
	public void addServiceStatusItem(ServiceStatusItem ssi){
		servicesStatus.add(ssi);
	}
	
}
