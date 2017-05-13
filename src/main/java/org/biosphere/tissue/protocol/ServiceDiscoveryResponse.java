package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDiscoveryResponse extends AbstractProtocol{
	
    //TODO add cell load maeasures to help choose the lease loaded cell
	
	@JsonProperty("isRunning")
	boolean isRunning;
	
	@JsonProperty("cellName")
	String cellName;
	
	@JsonProperty("cellNetworkName")
	String cellNetworkName;
	
	@JsonProperty("cellServicePort")
	int cellServicePort;
	
	@JsonProperty("cellNetworkName")
	public final String getCellNetworkName() {
		return cellNetworkName;
	}
	
	@JsonProperty("cellNetworkName")
	public final void setCellNetworkName(String cellNetworkName) {
		this.cellNetworkName = cellNetworkName;
	}
	
	@JsonProperty("cellServicePort")
	public final int getCellServicePort() {
		return cellServicePort;
	}
	
	@JsonProperty("cellServicePort")
	public final void setCellServicePort(int cellServicePort) {
		this.cellServicePort = cellServicePort;
	}
	
	@JsonProperty("isRunning")
	public final boolean isRunning() {
		return isRunning;
	}
	
	@JsonProperty("isRunning")
	public final void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}

	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	

}
