package org.biosphere.tissue.services;

import java.util.Hashtable;

public class ServiceDefinition {
	private String name;
	private String className;
	private String type;
	private boolean daemon = true;
	private String version;
	private Hashtable<String, Object> parameters;

	public ServiceDefinition() {
		super();
		parameters = new Hashtable<String, Object>();
	}

	public final void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	public final void removeParameter(String key) {
		parameters.remove(key);
	}

	public final Hashtable<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Hashtable<String, Object> parameters) {
		this.parameters = parameters;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final void setClassName(String className) {
		this.className = className;
	}

	public final String getClassName() {
		return className;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final String getType() {
		return type;
	}

	public final void setVersion(String version) {
		this.version = version;
	}

	public final String getVersion() {
		return version;
	}

	public final void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public final boolean isDaemon() {
		return daemon;
	}
}
