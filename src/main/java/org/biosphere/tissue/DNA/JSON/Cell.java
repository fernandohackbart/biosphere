package org.biosphere.tissue.DNA.JSON;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cell {
	@JsonProperty("name")
	String name;
	@JsonProperty("publicKey")
	String publicKey;
	@JsonProperty("tissuePort")
	String tissuePort;
	@JsonProperty("interfaces")
	ArrayList<CellNetworkInterface> interfaces;
	@JsonProperty("name")
	final String getName() {
		return name;
	}
	@JsonProperty("name")
	final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("publicKey")
	final String getPublicKey() {
		return publicKey;
	}
	@JsonProperty("publicKey")
	final void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	@JsonProperty("tissuePort")
	final String getTissuePort() {
		return tissuePort;
	}
	@JsonProperty("tissuePort")
	final void setTissuePort(String tissuePort) {
		this.tissuePort = tissuePort;
	}
	@JsonProperty("interfaces")
	final ArrayList<CellNetworkInterface> getInterfaces() {
		return interfaces;
	}
	@JsonProperty("interfaces")
	final void setInterfaces(ArrayList<CellNetworkInterface> interfaces) {
		this.interfaces = interfaces;
	}
}
