package org.biosphere.tissue.protocol;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueStatusResponse extends AbstractProtocol {

	public TissueStatusResponse() {
		super();
	}
	
	@JsonProperty("tissueName")
	String tissueName;
	@JsonProperty("tissueSize")
	int tissueSize;
	@JsonProperty("tissueCells")
	List<CellInterface> tissueCells;

	
	@JsonProperty("tissueName")
	public final String getTissueName() {
		return tissueName;
	}
	@JsonProperty("tissueName")
	public final void setTissueName(String tissueName) {
		this.tissueName = tissueName;
	}
	@JsonProperty("tissueSize")
	public final int getTissueSize() {
		return tissueSize;
	}
	@JsonProperty("tissueSize")
	public final void setTissueSize(int tissueSize) {
		this.tissueSize = tissueSize;
	}
	@JsonProperty("tissueCells")
	public final List<CellInterface> getTissueCells() {
		return tissueCells;
	}
	@JsonProperty("tissueCells")
	public final void setTissueCells(List<CellInterface> tissueCells) {
		this.tissueCells = tissueCells;
	}


}
