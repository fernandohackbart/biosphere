package org.biosphere.tissue.protocol;

import org.biosphere.tissue.DNA.Cell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueAddCellPayload extends Cell {
	@JsonProperty("operation")
	String operation;
	
	@JsonProperty("adopterCellName")
	String adopterCellName;

	@JsonProperty("adopterCellName")
	public final String getAdopterCellName() {
		return adopterCellName;
	}

	@JsonProperty("adopterCellName")
	public final void setAdopterCellName(String adopterCellName) {
		this.adopterCellName = adopterCellName;
	}
	
	@JsonProperty("operation")
	public final String getOperation() {
		return operation;
	}

	@JsonProperty("operation")
	public final void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void setCell(Cell cell)
	{
		setName(cell.getName());
		setPublicKey(cell.getPublicKey());
		setTissuePort(cell.getTissuePort());
		setInterfaces(cell.getInterfaces());
	}
	
}
