package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueGreeting {
	@JsonProperty("message")
	String message;
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("message")
	public final String getMessage() {
		return message;
	}
	@JsonProperty("message")
	public final void setMessage(String message) {
		this.message = message;
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
