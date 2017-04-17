package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TissueDNALocator {
	@JsonProperty("xmlDNAURL")
	String xmlDNAURL;
	@JsonProperty("jsonDNAURL")
	String jsonDNAURL;
	@JsonProperty("xmlDNAURL")
	public final String getXmlDNAURL() {
		return xmlDNAURL;
	}
	@JsonProperty("xmlDNAURL")
	public final void setXmlDNAURL(String xmlDNAURL) {
		this.xmlDNAURL = xmlDNAURL;
	}
	@JsonProperty("jsonDNAURL")
	public final String getJsonDNAURL() {
		return jsonDNAURL;
	}
	@JsonProperty("jsonDNAURL")
	public final void setJsonDNAURL(String jsonDNAURL) {
		this.jsonDNAURL = jsonDNAURL;
	}
}
