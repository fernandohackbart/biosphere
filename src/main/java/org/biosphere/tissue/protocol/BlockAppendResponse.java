package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockAppendResponse {
	
	public BlockAppendResponse() {
		super();
	}
	
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("accepted")
	boolean accepted;
	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	@JsonProperty("accepted")
	public final boolean isAccepted() {
		return accepted;
	}
	@JsonProperty("accepted")
	public final void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	

}
