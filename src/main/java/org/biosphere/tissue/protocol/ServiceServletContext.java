package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
/**
 * Class used by ServiceAddContextHandler to receive service contexts adding
 * 
 * @author Fernando Hackbart
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceServletContext {
	
	@JsonProperty("serviceName")
	String serviceName;
	@JsonProperty("className")
	String className;
	@JsonProperty("contentType")
	String contentType;
	@JsonProperty("contextURIs")
	ArrayList<ServiceServletContextURI> contextURIs;
	
	@JsonProperty("serviceName")
	public final String getServiceName() {
		return serviceName;
	}
	@JsonProperty("serviceName")
	public final void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	@JsonProperty("className")
	public final String getClassName() {
		return className;
	}
	@JsonProperty("className")
	public final void setClassName(String className) {
		this.className = className;
	}
	@JsonProperty("contentType")
	public final String getContentType() {
		return contentType;
	}
	@JsonProperty("contentType")
	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}
	@JsonProperty("contextURIs")
	public final ArrayList<ServiceServletContextURI> getContextURIs() {
		return contextURIs;
	}
	@JsonProperty("contextURIs")
	public final void setContextURIs(ArrayList<ServiceServletContextURI> contextURIs) {
		this.contextURIs = contextURIs;
	}

}
