package org.biosphere.tissue.DNA.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CellNetworkInterface {
	@JsonProperty("ipAddress")
	String ipAddress;
	@JsonProperty("hostname")
	String hostname;
	@JsonProperty("deviceName")
	String deviceName;
	@JsonProperty("ipAddress")
	final String getIpAddress() {
		return ipAddress;
	}
	@JsonProperty("ipAddress")
	final void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	@JsonProperty("hostname")
	final String getHostname() {
		return hostname;
	}
	@JsonProperty("hostname")
	final void setHostname(String hostname) {
		this.hostname = hostname;
	}
	@JsonProperty("deviceName")
	final String getDeviceName() {
		return deviceName;
	}
	@JsonProperty("deviceName")
	final void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
