package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used by ServiceAddContextHandler to receive service contexts adding
 * 
 * @author Fernando Hackbart
 *
 */
public class ServiceServletContextURI {
	
	@JsonProperty("contextURI")
	String contextURI;

	@JsonProperty("contextURI")
	public final String getContextURI() {
		return contextURI;
	}
	@JsonProperty("contextURI")
	public final void setContextURI(String contextURI) {
		this.contextURI = contextURI;
	}

}
