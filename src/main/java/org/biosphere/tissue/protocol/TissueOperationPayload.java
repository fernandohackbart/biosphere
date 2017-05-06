package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueOperationPayload {

	@JsonProperty("operation")
	String operation;
	
	@JsonProperty("operation")
	public final String getOperation() {
		return operation;
	}

	@JsonProperty("operation")
	public final void setOperation(String operation) {
		this.operation = operation;
	}
}
