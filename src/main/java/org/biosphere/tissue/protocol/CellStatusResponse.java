package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CellStatusResponse extends AbstractProtocol {

	public CellStatusResponse() {
		super();
	}
	
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("cellNetworkName")
	String cellNetworkName;
	@JsonProperty("cellTissuePort")
	int cellTissuePort;
	@JsonProperty("tissueName")
	String tissueName;
	@JsonProperty("tissueSize")
	int tissueSize;
	@JsonProperty("maxMemory")
	long maxMemory;
	@JsonProperty("allocatedMemory")
	long allocatedMemory;
	@JsonProperty("freeMemory")
	long freeMemory;
	
	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	@JsonProperty("cellNetworkName")
	public final String getCellNetworkName() {
		return cellNetworkName;
	}
	@JsonProperty("cellNetworkName")
	public final void setCellNetworkName(String cellNetworkName) {
		this.cellNetworkName = cellNetworkName;
	}
	@JsonProperty("cellTissuePort")
	public final int getCellTissuePort() {
		return cellTissuePort;
	}
	@JsonProperty("cellTissuePort")
	public final void setCellTissuePort(int cellTissuePort) {
		this.cellTissuePort = cellTissuePort;
	}
	@JsonProperty("tissueName")
	public final String getTissueName() {
		return tissueName;
	}
	@JsonProperty("tissueName")
	public final void setTissueName(String tissueName) {
		this.tissueName = tissueName;
	}
	@JsonProperty("tissueSize")
	public final int getTissueSize() {
		return tissueSize;
	}
	@JsonProperty("tissueSize")
	public final void setTissueSize(int tissueSize) {
		this.tissueSize = tissueSize;
	}
	@JsonProperty("maxMemory")
	public final long getMaxMemory() {
		return maxMemory;
	}
	@JsonProperty("cellName")
	public final void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	@JsonProperty("freeMemory")
	public final long getFreeMemory() {
		return freeMemory;
	}
	@JsonProperty("freeMemory")
	public final void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}
	@JsonProperty("allocatedMemory")
	public final long getAllocatedMemory() {
		return allocatedMemory;
	}
	@JsonProperty("allocatedMemory")
	public final void setAllocatedMemory(long allocatedMemory) {
		this.allocatedMemory = allocatedMemory;
	}	
}
