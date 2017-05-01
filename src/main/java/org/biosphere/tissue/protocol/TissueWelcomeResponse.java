package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueWelcomeResponse {
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("cellBusy")
	boolean cellBusy;
	@JsonProperty("message")
	String message;
	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	@JsonProperty("cellBusy")
	public final boolean isCellBusy() {
		return cellBusy;
	}
	@JsonProperty("cellBusy")
	public final void setCellBusy(boolean cellBusy) {
		this.cellBusy = cellBusy;
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
