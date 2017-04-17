package org.biosphere.tissue.DNA.JSON;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tissue {
	
	public Tissue() {
		super();
	    cells = new ArrayList<Cell>(); 
	    services = new ArrayList<Service>();
	}
	
	@JsonProperty("name")
	String name;
	@JsonProperty("dnaVersion")
	String dnaVersion;
	@JsonProperty("defaultMulticastPort")
	String defaultMulticastPort;
	@JsonProperty("defaultMulticastAddress")
	String defaultMulticastAddress;
	@JsonProperty("defaultListenerPort")
	int defaultListenerPort;
	@JsonProperty("cells")
	ArrayList<Cell> cells;	
	@JsonProperty("services")
	ArrayList<Service> services;
	@JsonProperty("name")
	final String getName() {
		return name;
	}
	@JsonProperty("name")
	final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("dnaVersion")
	final String getDnaVersion() {
		return dnaVersion;
	}
	@JsonProperty("dnaVersion")
	final void setDnaVersion(String dnaVersion) {
		this.dnaVersion = dnaVersion;
	}
	@JsonProperty("defaultMulticastPort")
	final String getDefaultMulticastPort() {
		return defaultMulticastPort;
	}
	@JsonProperty("defaultMulticastPort")
	final void setDefaultMulticastPort(String defaultMulticastPort) {
		this.defaultMulticastPort = defaultMulticastPort;
	}
	@JsonProperty("defaultMulticastAddress")
	final String getDefaultMulticastAddress() {
		return defaultMulticastAddress;
	}
	@JsonProperty("defaultMulticastAddress")
	final void setDefaultMulticastAddress(String defaultMulticastAddress) {
		this.defaultMulticastAddress = defaultMulticastAddress;
	}
	@JsonProperty("defaultListenerPort")
	final int getDefaultListenerPort() {
		return defaultListenerPort;
	}
	@JsonProperty("defaultListenerPort")
	final void setDefaultListenerPort(int defaultListenerPort) {
		this.defaultListenerPort = defaultListenerPort;
	}
	@JsonProperty("cells")
	final ArrayList<Cell> getCells() {
		return cells;
	}
	@JsonProperty("cells")
	final void setCells(ArrayList<Cell> cells) {
		this.cells = cells;
	}
	@JsonProperty("services")
	final ArrayList<Service> getServices() {
		return services;
	}
	@JsonProperty("services")
	final void setServices(ArrayList<Service> services) {
		this.services = services;
	}
}
