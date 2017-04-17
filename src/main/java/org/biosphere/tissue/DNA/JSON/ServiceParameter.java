package org.biosphere.tissue.DNA.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceParameter {
	@JsonProperty("name")
	String name;
	@JsonProperty("value")
	Object value;
	@JsonProperty("name")
	final String getName() {
		return name;
	}
	@JsonProperty("name")
	final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("value")
	final Object getValue() {
		return value;
	}
	@JsonProperty("value")
	final void setValue(Object value) {
		this.value = value;
	}
}
