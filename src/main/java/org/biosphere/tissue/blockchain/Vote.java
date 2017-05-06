package org.biosphere.tissue.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {
	public Vote() {
		super();
	}

	public Vote(String cellID, boolean accepted) {
		setCellID(cellID);
		setAccepted(accepted);
	}

	@JsonProperty("cellID")
	private String cellID;
	
	@JsonProperty("accepted")
	private boolean accepted;

	@JsonProperty("cellID")
	public final void setCellID(String cellID) {
		this.cellID = cellID;
	}

	@JsonProperty("cellID")
	public final String getCellID() {
		return cellID;
	}
	
	@JsonProperty("accepted")
	public final void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	@JsonProperty("accepted")
	public final boolean isAccepted() {
		return accepted;
	}
}
