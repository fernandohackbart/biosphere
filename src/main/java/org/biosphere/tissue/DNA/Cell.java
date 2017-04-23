package org.biosphere.tissue.DNA;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cell {
	
	public Cell() {
		super();
		interfaces = new ArrayList<CellNetworkInterface>();
	}
	
	@JsonProperty("name")
	String name;
	@JsonProperty("publicKey")
	String publicKey;
	@JsonProperty("tissuePort")
	int tissuePort;
	@JsonProperty("interfaces")
	ArrayList<CellNetworkInterface> interfaces;
	@JsonProperty("name")
	public final String getName() {
		return name;
	}
	@JsonProperty("name")
	public final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("publicKey")
	public final String getPublicKey() {
		return publicKey;
	}
	@JsonProperty("publicKey")
	public final void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	@JsonProperty("tissuePort")
	public final int getTissuePort() {
		return tissuePort;
	}
	@JsonProperty("tissuePort")
	public final void setTissuePort(int tissuePort) {
		this.tissuePort = tissuePort;
	}
	@JsonProperty("interfaces")
	public final ArrayList<CellNetworkInterface> getInterfaces() {
		return interfaces;
	}
	@JsonProperty("interfaces")
	public final void setInterfaces(ArrayList<CellNetworkInterface> interfaces) {
		this.interfaces = interfaces;
	}
}
