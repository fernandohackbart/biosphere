package org.biosphere.tissue.DNA;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {

	public Service() {
		super();
		parameters = new ArrayList<ServiceParameter>();
	}

	@JsonProperty("name")
	private String name;
	@JsonProperty("version")
	private String version;
	@JsonProperty("type")
	private String type;
	@JsonProperty("enabled")
	private boolean enabled;
	@JsonProperty("daemon")
	private boolean daemon;
	@JsonProperty("className")
	private String className;
	@JsonProperty("parameters")
	private ArrayList<ServiceParameter> parameters;
	@JsonProperty("coreService")
	private boolean coreService;
	
	
	@JsonProperty("name")
	public final String getName() {
		return name;
	}

	@JsonProperty("name")
	public final void setName(String name) {
		this.name = name;
	}

	@JsonProperty("version")
	public final String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public final void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("type")
	public final String getType() {
		return type;
	}

	@JsonProperty("type")
	public final void setType(String type) {
		this.type = type;
	}

	@JsonProperty("daemon")
	public final boolean isDaemon() {
		return daemon;
	}

	@JsonProperty("daemon")
	public final void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	@JsonProperty("className")
	public final String getClassName() {
		return className;
	}

	@JsonProperty("className")
	public final void setClassName(String className) {
		this.className = className;
	}

	@JsonProperty("parameters")
	public final ArrayList<ServiceParameter> getParameters() {
		return parameters;
	}

	@JsonProperty("parameters")
	public final void setParameters(ArrayList<ServiceParameter> parameters) {
		this.parameters = parameters;
	}

	@JsonProperty("enabled")
	public final boolean isEnabled() {
		return enabled;
	}

	@JsonProperty("enabled")
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@JsonProperty("coreService")
	public final boolean isCoreService() {
		return coreService;
	}
	@JsonProperty("coreService")
	public final void setCoreService(boolean coreService) {
		this.coreService = coreService;
	}
	
    @JsonIgnore
	public final void addParameter(String key, Object value) throws IOException {
		if (!containsParameter(key)) {
			ServiceParameter sp = new ServiceParameter();
			sp.setName(key);
			sp.setObjectValue(value);
			parameters.add(sp);
		}
	}

    @JsonIgnore
	public final boolean containsParameter(String key) {
		boolean present = false;
		for (ServiceParameter sp : getParameters()) {
			if (sp.getName().equals(key)) {
				present = true;
				break;
			}
		}
		return present;
	}

    @JsonIgnore
	public final Object getParameterValue(String key) throws IOException {
		Object value = null;
		if (containsParameter(key)) {
			for (ServiceParameter sp : getParameters()) {
				if (sp.getName().equals(key)) {
					value = sp.getObjectValue();
					break;
				}
			}
		}
		return value;
	}
    
    @JsonIgnore
	public final boolean setParameterValue(String key,Object value) throws IOException {
    	boolean parameterChanged=false;
		if (containsParameter(key)) {
			for (ServiceParameter sp : getParameters()) {
				if (sp.getName().equals(key)) {
					sp.setObjectValue(value);
					parameterChanged=true;
					break;
				}
			}
		}
		return parameterChanged;
	}

	@JsonIgnore 
	public final void removeParameter(String key) {
		for (ServiceParameter sp : getParameters()) {
			if (sp.getName().equals(key)) {
				// TODO remove the entry from the Array
				// value=sp.getObjectValue();
			}
		}
	}
}
