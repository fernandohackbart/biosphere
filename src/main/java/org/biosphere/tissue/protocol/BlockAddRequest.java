package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockAddRequest {
	
	@JsonProperty("payload")
	String payload;

	@JsonProperty("payload")
	public final String getPayload() {
		return payload;
	}

	@JsonProperty("payload")
	public final void setPayload(String payload) {
		this.payload = payload;
	}
}
