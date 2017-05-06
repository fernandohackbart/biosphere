package org.biosphere.tissue.protocol;

import org.biosphere.tissue.DNA.Cell;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueRemoveCellPayload extends TissueOperationPayload{
	@JsonProperty("requesterCellName")
	String requesterCellName;

	@JsonProperty("toRemoveCell")
	Cell toRemoveCell;
	
	@JsonProperty("requesterCellName")
	public final String getRequesterCellName() {
		return requesterCellName;
	}

	@JsonProperty("requesterCellName")
	public final void setRequesterCellName(String requesterCellName) {
		this.requesterCellName = requesterCellName;
	}

	@JsonProperty("toRemoveCell")
	public final Cell getToRemoveCell() {
		return toRemoveCell;
	}
	@JsonProperty("toRemoveCell")
	public final void setToRemoveCell(Cell toRemoveCell) {
		this.toRemoveCell = toRemoveCell;
	}

}
