package org.biosphere.tissue.protocol;

import org.biosphere.tissue.DNA.Cell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueAddCellPayload extends TissueOperationPayload {

	@JsonProperty("cell")
	Cell cell;

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

	@JsonProperty("cell")
	public final Cell getCell() {
		return cell;
	}

	@JsonProperty("cell")
	public final void setCell(Cell cell) {
		this.cell = cell;
	}
}
