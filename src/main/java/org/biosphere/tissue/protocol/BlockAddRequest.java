package org.biosphere.tissue.protocol;

import org.bouncycastle.util.encoders.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BlockAddRequest extends AbstractProtocol{
	
	public BlockAddRequest() {
		super();
	}
	
	@JsonProperty("title")
	String title;

	@JsonProperty("payload")
	String payload;
	
	@JsonProperty("ensureAcceptance")
	boolean ensureAcceptance;
	
	@JsonProperty("title")
	public final String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public final void setTitle(String title) {
		this.title = title;
	}
	
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
	
	public void setPayload(TissueOperationPayload payload) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		setPayload(Base64.toBase64String(mapper.writeValueAsString(payload).getBytes()));
	}
}
