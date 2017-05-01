package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueAnnounce extends AbstractProtocol{
	
	public TissueAnnounce() {
		super();
	}
	
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("cellNetworkName")
	String cellNetworkName;
	@JsonProperty("tissuePort")
	int tissuePort;
	@JsonProperty("cellCertificate")
	String cellCertificate;
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
	@JsonProperty("tissuePort")
	public final int getTissuePort() {
		return tissuePort;
	}
	@JsonProperty("tissuePort")
	public final void setTissuePort(int tissuePort) {
		this.tissuePort = tissuePort;
	}
	@JsonProperty("cellCertificate")
	public final String getCellCertificate() {
		return cellCertificate;
	}
	@JsonProperty("cellCertificate")
	public final void setCellCertificate(String cellCertificate) {
		this.cellCertificate = cellCertificate;
	}
}
