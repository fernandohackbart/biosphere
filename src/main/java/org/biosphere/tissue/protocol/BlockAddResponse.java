package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockAddResponse extends AbstractProtocol {

	public BlockAddResponse() {
		super();
	}
	
	@JsonProperty("accepted")
	boolean accepted;
	@JsonProperty("cellName")
	String cellName;
	@JsonProperty("blockID")
	String blockID;
	@JsonProperty("accepted")
	public final boolean isAccepted() {
		return accepted;
	}
	@JsonProperty("accepted")
	public final void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	@JsonProperty("cellName")
	public final String getCellName() {
		return cellName;
	}
	@JsonProperty("cellName")
	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}
	@JsonProperty("blockID")
	public final String getBlockID() {
		return blockID;
	}
	@JsonProperty("blockID")
	public final void setBlockID(String blockID) {
		this.blockID = blockID;
	}
	

}
