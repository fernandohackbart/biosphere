package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueWelcomeRequest {
	@JsonProperty("tissueName")
	String tissueName;
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("cellCertificate")
	String cellCertificate;
	@JsonProperty("tissueName")
	public final String getTissueName() {
		return tissueName;
	}
	@JsonProperty("tissueName")
	public final void setTissueName(String tissueName) {
		this.tissueName = tissueName;
	}
	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
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
