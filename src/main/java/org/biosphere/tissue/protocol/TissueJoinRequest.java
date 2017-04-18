package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueJoinRequest {
	@JsonProperty("jsonDNAURL")
	String jsonDNAURL;
	@JsonProperty("dna")
	String dna;
	@JsonProperty("chain")
	String chain;
	@JsonProperty("jsonDNAURL")
	public final String getJsonDNAURL() {
		return jsonDNAURL;
	}
	@JsonProperty("jsonDNAURL")
	public final void setJsonDNAURL(String jsonDNAURL) {
		this.jsonDNAURL = jsonDNAURL;
	}
	@JsonProperty("dna")
	public final String getDna() {
		return dna;
	}
	@JsonProperty("dna")
	public final void setDna(String dna) {
		this.dna = dna;
	}
	@JsonProperty("chain")
	public final String getChain() {
		return chain;
	}
	@JsonProperty("chain")
	public final void setChain(String chain) {
		this.chain = chain;
	}
}
