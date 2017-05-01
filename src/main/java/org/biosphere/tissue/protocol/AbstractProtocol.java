package org.biosphere.tissue.protocol;

import java.util.UUID;

public abstract class AbstractProtocol {

	public AbstractProtocol() {
		super();
		setRequestID(UUID.randomUUID().toString());
	}
	
	String requestID;

	public final String getRequestID() {
		return requestID;
	}

	public final void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
}

