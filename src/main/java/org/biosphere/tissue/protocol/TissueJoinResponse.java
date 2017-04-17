package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueJoinResponse {
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("message")
	String message;
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	@JsonProperty("message")
	public final String getMessage() {
		return message;
	}
	@JsonProperty("message")
	public final void setMessage(String message) {
		this.message = message;
	}

}
