package org.biosphere.tissue.DNA.JSON;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tissue {
	@JsonProperty("name")
	String name;
	@JsonProperty("dnaVersion")
	String dnaVersion;
	@JsonProperty("defaultMulticastGroup")
	String defaultMulticastGroup;
	@JsonProperty("defaultMulticastAddress")
	String defaultMulticastAddress;
	@JsonProperty("defaultListenerPort")
	String defaultListenerPort;
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
	@JsonProperty("defaultMulticastGroup")
	final String getDefaultMulticastGroup() {
		return defaultMulticastGroup;
	}
	@JsonProperty("defaultMulticastGroup")
	final void setDefaultMulticastGroup(String defaultMulticastGroup) {
		this.defaultMulticastGroup = defaultMulticastGroup;
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
	final String getDefaultListenerPort() {
		return defaultListenerPort;
	}
	@JsonProperty("defaultListenerPort")
	final void setDefaultListenerPort(String defaultListenerPort) {
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
