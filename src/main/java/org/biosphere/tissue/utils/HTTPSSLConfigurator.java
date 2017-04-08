package org.biosphere.tissue.utils;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;

public class HTTPSSLConfigurator extends HttpsConfigurator {

	public HTTPSSLConfigurator(SSLContext sslContext) {
		super(sslContext);
	}

	public void configure(HttpsParameters params) {
		// To enable strong ciphers
		// http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

		Logger logger = new Logger();
		boolean needClientAuth = false;
		String cipherSuiters = "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384";
		String protocols = "TLSv1.2";

		try {
			SSLContext c = SSLContext.getDefault();

			// params.setCipherSuites(engine.getEnabledCipherSuites());
			// params.setProtocols(engine.getEnabledProtocols());

			// Only accepts strong ciphers!!!
			logger.debugSSL("HTTPSSLConfigurator.configure()", "setNeedClientAuth()=" + needClientAuth);
			params.setNeedClientAuth(needClientAuth);
			logger.debugSSL("HTTPSSLConfigurator.configure()", "setCipherSuites()=" + cipherSuiters);
			params.setCipherSuites(new String[] { cipherSuiters });
			logger.debugSSL("HTTPSSLConfigurator.configure()", "setProtocols()=" + protocols);
			params.setProtocols(new String[] { protocols });

			SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
			params.setSSLParameters(defaultSSLParameters);
		} catch (Exception e) {
			TissueExceptionHandler.handleGenericException(e, "HTTPSSLConfigurator.configure()", e.getMessage());
		}
	}
}
