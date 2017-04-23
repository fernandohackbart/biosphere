package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockAddRequest {

	@JsonProperty("payload")
	String payload;
	
	@JsonProperty("ensureAcceptance")
	boolean ensureAcceptance;

	@JsonProperty("payload")
	public final String getPayload() {
		return payload;
	}

	@JsonProperty("payload")
	public final void setPayload(String payload) {
		this.payload = payload;
	}

	@JsonProperty("ensureAcceptance")
	public final boolean isEnsureAcceptance() {
		return ensureAcceptance;
	}

	@JsonProperty("ensureAcceptance")
	public final void setEnsureAcceptance(boolean ensureAcceptance) {
		this.ensureAcceptance = ensureAcceptance;
	}
}
