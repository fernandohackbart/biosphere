package org.biosphere.tissue.DNA;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
	
	public Service() {
		super();
		parameters= new ArrayList<ServiceParameter>();
	}
	@JsonProperty("name")
	String name;
	@JsonProperty("version")
	String version;
	@JsonProperty("type")
	String type;
	@JsonProperty("daemon")
	boolean daemon;
	@JsonProperty("className")
	String className;
	@JsonProperty("parameters")
	ArrayList<ServiceParameter> parameters;
	@JsonProperty("name")
	final String getName() {
		return name;
	}
	@JsonProperty("name")
	final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("version")
	final String getVersion() {
		return version;
	}
	@JsonProperty("version")
	final void setVersion(String version) {
		this.version = version;
	}
	@JsonProperty("type")
	final String getType() {
		return type;
	}
	@JsonProperty("type")
	final void setType(String type) {
		this.type = type;
	}
	@JsonProperty("daemon")
	final boolean isDaemon() {
		return daemon;
	}
	@JsonProperty("daemon")
	final void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
	@JsonProperty("className")
	final String getClassName() {
		return className;
	}
	@JsonProperty("className")
	final void setClassName(String className) {
		this.className = className;
	}
	@JsonProperty("parameters")
	final ArrayList<ServiceParameter> getParameters() {
		return parameters;
	}
	@JsonProperty("parameters")
	final void setParameters(ArrayList<ServiceParameter> parameters) {
		this.parameters = parameters;
	}
}
