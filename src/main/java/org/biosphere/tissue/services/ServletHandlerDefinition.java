package org.biosphere.tissue.services;

import java.io.Serializable;
import java.util.ArrayList;

public class ServletHandlerDefinition implements Serializable{

	private static final long serialVersionUID = 1L;
	String className;
	String contentType;
	String contentEncoding;
	ArrayList<String> contexts;
	public final String getClassName() {
		return className;
	}
	public final void setClassName(String className) {
		this.className = className;
	}
	public final String getContentType() {
		return contentType;
	}
	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public final String getContentEncoding() {
		return contentEncoding;
	}
	public final void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	public final ArrayList<String> getContexts() {
		return contexts;
	}
	public final void setContexts(ArrayList<String> contexts) {
		this.contexts = contexts;
	}
}
