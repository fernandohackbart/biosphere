package org.biosphere.tissue.utils;

import javax.net.ssl.X509TrustManager;

public class RelaxedTrustManager implements X509TrustManager {
	public RelaxedTrustManager() {
		super();
	}

	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	}

	public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	}
}
