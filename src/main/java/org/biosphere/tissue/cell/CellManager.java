package org.biosphere.tissue.cell;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigInteger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;

import java.util.ArrayList;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.Service;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.services.ServletHandlerDefinition;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.KeystoreManager;
import org.biosphere.tissue.utils.RelaxedTrustManager;

import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CellManager {
	public CellManager() {
		super();
	}

	public final static void setupCell(Cell cell) {
		System.setProperty(TissueManager.logLevelParameter, TissueManager.logLevelValue);
		System.setProperty(TissueManager.logOutputParameter, TissueManager.logOutputValue);
		System.setProperty(TissueManager.logShowDateTimeParameter, TissueManager.logShowDateTimeValue);
		System.setProperty(TissueManager.logDateFormatParameter, TissueManager.logDateFormatValue);
		System.setProperty(TissueManager.jettyLogLevelParameter, TissueManager.jettyLogLevelValue);
		System.setProperty(TissueManager.jettyLogOutputParameter, TissueManager.jettyLogOutputValue);
		cell.setCellName(generateCellName());
		cell.setCellNetworkName(getCellNetworkName());
		cell.setTissueMember(false);
		cell.setTissuePort(TissueManager.defaultTissuePort);
		cell.setCellKeystorePWD(generateCellKeystorePWD());
	}

	private static String generateCellName() {
		return generateCellRandomName();
	}

	private static String generateCellRandomName() {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		String cellName = UUID.randomUUID().toString();
		logger.info("CellManager.generateCellName() Cell name: " + cellName);
		return cellName;
	}

	public static String getCellNetworkName() {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		String cellName = "localhost";
		try {
			String networkName = InetAddress.getLocalHost().getHostName();
			cellName = networkName;
			logger.info("CellManager.generateCellName() Cell network name: " + cellName);
		} catch (UnknownHostException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.gatCellNetworkName()",
					"Failed to generate Cell name due to UnknowHost Exception!");
		}
		return cellName;
	}

	public final static void stopCell(Cell thisCell) {
		try {
			try {
				thisCell.getDna().removeCell(thisCell.getCellName(), thisCell.getCellCertificate(), thisCell.getCellNetworkName(), thisCell.getTissuePort(),thisCell.getChain());
			} catch (JsonProcessingException | BlockException e) {
				TissueExceptionHandler.handleGenericException(e, "CellManager.stopCell()", "Failed to notify the DNA.");
			}
			ServiceManager.stop(TissueManager.ThreadServiceClass, "CellMonitor");
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.stopCell()", "Failed to stop Cell.");
		}
	}

	public final static Service getCellMonitorDefinition() throws IOException {
		Service sdCellMonitor = new Service();
		sdCellMonitor.setName("CellMonitor");
		sdCellMonitor.setType(TissueManager.ThreadServiceClass);
		sdCellMonitor.setVersion("0.1");
		sdCellMonitor.setDaemon(false);
		sdCellMonitor.setEnabled(true);
		sdCellMonitor.setClassName("org.biosphere.tissue.services.CellMonitor");
		sdCellMonitor.addParameter("Interval", TissueManager.monitorInterval);
		return sdCellMonitor;
	}

	public final static Service getCellAnnounceListenerDefinition() throws IOException {
		Service sdCellAnnounceListener = new Service();
		sdCellAnnounceListener.setName("CellAnnounceListener");
		sdCellAnnounceListener.setType(TissueManager.ThreadServiceClass);
		sdCellAnnounceListener.setVersion("0.1");
		sdCellAnnounceListener.setEnabled(true);
		sdCellAnnounceListener.setClassName("org.biosphere.tissue.services.CellAnnounceListener");
		sdCellAnnounceListener.addParameter("AnnouncePort", TissueManager.announcePort);
		sdCellAnnounceListener.addParameter("AnnounceAddress", TissueManager.announceAddress);
		return sdCellAnnounceListener;
	}

	public final static Service getCellACSDefinition() throws IOException {
		Service sdCellACS = new Service();
		sdCellACS.setName("CellAdministrationConsole");
		sdCellACS.setType(TissueManager.ThreadServiceClass);
		sdCellACS.setVersion("0.1");
		sdCellACS.setEnabled(true);
		sdCellACS.setClassName("org.biosphere.tissue.services.CellAdministrationConsole");
		sdCellACS.addParameter("ListenPort", TissueManager.announcePort);
		return sdCellACS;
	}

	public final static Service getCellTissueServletListenerDefinition() throws IOException {
		ArrayList<ServletHandlerDefinition> cellTissueListenerHandlers = new ArrayList<ServletHandlerDefinition>();

		ServletHandlerDefinition cellTissueWelcomeSHD = new ServletHandlerDefinition();
		cellTissueWelcomeSHD.setClassName("org.biosphere.tissue.handlers.CellTissueWelcomeHandler");
		cellTissueWelcomeSHD.setContentType("application/json");
		cellTissueWelcomeSHD.setContentEncoding("utf-8");
		ArrayList<String> cellTissueWelcomeContexts = new ArrayList<String>();
		cellTissueWelcomeContexts.add("/org/biosphere/tissue/welcome");
		cellTissueWelcomeSHD.setContexts(cellTissueWelcomeContexts);
		cellTissueListenerHandlers.add(cellTissueWelcomeSHD);

		ServletHandlerDefinition cellTissueJoinSHD = new ServletHandlerDefinition();
		cellTissueJoinSHD.setClassName("org.biosphere.tissue.handlers.CellTissueJoinHandler");
		cellTissueJoinSHD.setContentType("application/json");
		cellTissueJoinSHD.setContentEncoding("utf-8");
		ArrayList<String> cellTissueJoinContexts = new ArrayList<String>();
		cellTissueJoinContexts.add("/org/biosphere/tissue/join");
		cellTissueJoinSHD.setContexts(cellTissueJoinContexts);
		cellTissueListenerHandlers.add(cellTissueJoinSHD);

		ServletHandlerDefinition cellStopSHD = new ServletHandlerDefinition();
		cellStopSHD.setClassName("org.biosphere.tissue.handlers.CellStopHandler");
		cellStopSHD.setContentType("text/html");
		cellStopSHD.setContentEncoding("utf-8");
		ArrayList<String> cellStopContexts = new ArrayList<String>();
		cellStopContexts.add("/org/biosphere/cell/stop");
		cellStopSHD.setContexts(cellStopContexts);
		cellTissueListenerHandlers.add(cellStopSHD);
		
		ServletHandlerDefinition serviceDiscoverSHD = new ServletHandlerDefinition();
		serviceDiscoverSHD.setClassName("org.biosphere.tissue.handlers.ServiceDiscoveryHandler");
		serviceDiscoverSHD.setContentType("application/json");
		serviceDiscoverSHD.setContentEncoding("utf-8");
		ArrayList<String> serviceDiscoverContexts = new ArrayList<String>();
		serviceDiscoverContexts.add("/org/biosphere/cell/service/discover");
		serviceDiscoverSHD.setContexts(serviceDiscoverContexts);
		cellTissueListenerHandlers.add(serviceDiscoverSHD);

		ServletHandlerDefinition serviceStopSHD = new ServletHandlerDefinition();
		serviceStopSHD.setClassName("org.biosphere.tissue.handlers.ServiceThreadStopHandler");
		serviceStopSHD.setContentType("text/html");
		serviceStopSHD.setContentEncoding("utf-8");
		ArrayList<String> serviceStopContexts = new ArrayList<String>();
		serviceStopContexts.add("/org/biosphere/cell/service/thread/stop");
		serviceStopSHD.setContexts(serviceStopContexts);
		cellTissueListenerHandlers.add(serviceStopSHD);

		ServletHandlerDefinition httpServiceStopSHD = new ServletHandlerDefinition();
		httpServiceStopSHD.setClassName("org.biosphere.tissue.handlers.ServiceServletStopHandler");
		httpServiceStopSHD.setContentType("text/html");
		httpServiceStopSHD.setContentEncoding("utf-8");
		ArrayList<String> httpServiceStopContexts = new ArrayList<String>();
		httpServiceStopContexts.add("/org/biosphere/cell/service/servlet/stop");
		httpServiceStopSHD.setContexts(httpServiceStopContexts);
		cellTissueListenerHandlers.add(httpServiceStopSHD);

		ServletHandlerDefinition cellStatusSHD = new ServletHandlerDefinition();
		cellStatusSHD.setClassName("org.biosphere.tissue.handlers.CellStatusHandler");
		cellStatusSHD.setContentType("text/plain");
		cellStatusSHD.setContentEncoding("utf-8");
		ArrayList<String> cellStatusContexts = new ArrayList<String>();
		cellStatusContexts.add("/org/biosphere/cell/status");
		cellStatusSHD.setContexts(cellStatusContexts);
		cellTissueListenerHandlers.add(cellStatusSHD);

		ServletHandlerDefinition chainAddBlockSHD = new ServletHandlerDefinition();
		chainAddBlockSHD.setClassName("org.biosphere.tissue.handlers.ChainAddBlockHandler");
		chainAddBlockSHD.setContentType("application/json");
		chainAddBlockSHD.setContentEncoding("utf-8");
		ArrayList<String> chainAddBlockContexts = new ArrayList<String>();
		chainAddBlockContexts.add("/org/biosphere/cell/chain/add/block");
		chainAddBlockSHD.setContexts(chainAddBlockContexts);
		cellTissueListenerHandlers.add(chainAddBlockSHD);

		ServletHandlerDefinition chainAppendBlockSHD = new ServletHandlerDefinition();
		chainAppendBlockSHD.setClassName("org.biosphere.tissue.handlers.ChainAppendBlockHandler");
		chainAppendBlockSHD.setContentType("application/json");
		chainAppendBlockSHD.setContentEncoding("utf-8");
		ArrayList<String> chainAppendBlockContexts = new ArrayList<String>();
		chainAppendBlockContexts.add("/org/biosphere/cell/chain/append/block");
		chainAppendBlockSHD.setContexts(chainAppendBlockContexts);
		cellTissueListenerHandlers.add(chainAppendBlockSHD);

		ServletHandlerDefinition chainGetImageChainSHD = new ServletHandlerDefinition();
		chainGetImageChainSHD.setClassName("org.biosphere.tissue.handlers.ChainGetImageChainHandler");
		chainGetImageChainSHD.setContentType("image/png");
		chainGetImageChainSHD.setContentEncoding("utf-8");
		ArrayList<String> chainGetImageChainContexts = new ArrayList<String>();
		chainGetImageChainContexts.add("/org/biosphere/cell/chain/get/chainimage");
		chainGetImageChainSHD.setContexts(chainGetImageChainContexts);
		cellTissueListenerHandlers.add(chainGetImageChainSHD);
		// #################################################################################################

		Service sdCellTissueListener = new Service();
		sdCellTissueListener.setName("CellTissueListener");
		sdCellTissueListener.setType(TissueManager.ServletServiceClass);
		sdCellTissueListener.setVersion("0.1");
		sdCellTissueListener.setEnabled(true);
		sdCellTissueListener.setClassName("org.eclipse.jetty.server.Server");
		sdCellTissueListener.addParameter("Handlers", cellTissueListenerHandlers);
		sdCellTissueListener.addParameter("DefaultHandler","org.biosphere.tissue.handlers.CellDefaultHandler");
		sdCellTissueListener.addParameter("DefaultHTTPPort", TissueManager.defaultTissuePort);
		return sdCellTissueListener;
	}

	public final static Service getCellServiceListenerDefinition() throws IOException {
		// all those parameters should be placed inside the DNA and the consumed
		// from there when starting the service

		ArrayList<ServletHandlerDefinition> cellServiceListenerHandlers = new ArrayList<ServletHandlerDefinition>();
		ServletHandlerDefinition cellServiceListenerSHD = new ServletHandlerDefinition();

		ServletHandlerDefinition cellServiceNotFoundSHD = new ServletHandlerDefinition();
		cellServiceNotFoundSHD.setClassName("org.biosphere.tissue.handlers.ServiceNotFoundHandler");
		cellServiceNotFoundSHD.setContentType("text/html");
		cellServiceNotFoundSHD.setContentEncoding("utf-8");
		ArrayList<String> cellServiceNotFoundContexts = new ArrayList<String>();
		cellServiceNotFoundContexts.add("/org/biosphere/cell/service/notfound");
		cellServiceNotFoundSHD.setContexts(cellServiceNotFoundContexts);
		//cellServiceListenerHandlers.add(cellServiceNotFoundSHD);		
		
		ServletHandlerDefinition cellHTTPContextAddSHD = new ServletHandlerDefinition();
		cellHTTPContextAddSHD.setClassName("org.biosphere.tissue.handlers.ServiceAddContextHandler");
		cellHTTPContextAddSHD.setContentType("text/plain");
		cellHTTPContextAddSHD.setContentEncoding("utf-8");
		ArrayList<String> cellHTTPContextAddContexts = new ArrayList<String>();
		cellHTTPContextAddContexts.add("/org/biosphere/cell/service/context/add");
		cellHTTPContextAddSHD.setContexts(cellHTTPContextAddContexts);
		cellServiceListenerHandlers.add(cellHTTPContextAddSHD);

		cellServiceListenerSHD.setClassName("org.biosphere.tissue.handlers.ServiceInstantiationHandler");
		cellServiceListenerSHD.setContentType("text/plain");
		cellServiceListenerSHD.setContentEncoding("utf-8");
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		chainParseChainContexts.add("/test");
		cellServiceListenerSHD.setContexts(chainParseChainContexts);
		cellServiceListenerHandlers.add(cellServiceListenerSHD);

		Service sdCellTissueListener = new Service();
		sdCellTissueListener.setName("CellServiceListener");
		sdCellTissueListener.setType(TissueManager.ServletServiceClass);
		sdCellTissueListener.setVersion("0.1");
		sdCellTissueListener.setEnabled(true);
		sdCellTissueListener.setClassName("org.eclipse.jetty.server.Server");
		sdCellTissueListener.addParameter("Handlers", cellServiceListenerHandlers);
		sdCellTissueListener.addParameter("DefaultHandler","org.biosphere.tissue.handlers.ServiceDefaultHandler");
		sdCellTissueListener.addParameter("DefaultHTTPPort",
				TissueManager.defaultTissuePort + TissueManager.portJumpFactor);
		return sdCellTissueListener;
	}

	public static final void startTissueListenerService(Cell cell) throws CellException, IOException {
		cell.setTissuePort(ServiceManager.startService(getCellTissueServletListenerDefinition(), cell));
	}

	public static final void loadServicesDNA(Cell cell) {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		logger.debug("CellManager.addServicesDNA() Adding CellMonitor definition to DNA");
		try {
			cell.getDna().addService(getCellMonitorDefinition());
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServicesDNA()", e.getLocalizedMessage());
		}
		logger.debug("CellManager.addServicesDNA() Adding CellAnnounceListener definition to DNA");
		try {
			cell.getDna().addService(getCellAnnounceListenerDefinition());
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServicesDNA()", e.getLocalizedMessage());
		}
		logger.debug("CellManager.addServicesDNA() Adding CellTissueListener definition to DNA");
		try {
			cell.getDna().addService(getCellTissueServletListenerDefinition());
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServicesDNA()", e.getLocalizedMessage());
		}
		// logger.debug("CellManager.addServicesDNA()","Adding CellACS definition to DNA");
		// cell.getDna().addService(getCellACSDefinition());
		logger.debug("CellManager.addServicesDNA() Adding CellServiceListener definition to DNA");
		try {
			cell.getDna().addService(getCellServiceListenerDefinition());
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServicesDNA()", e.getLocalizedMessage());
		}
	}

	public static final void startServicesDNA(Cell cell) {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		ArrayList<Service> sds = cell.getDna().getServices();
		for (Service sd : sds) {
			logger.info("CellManager.startServicesDNA() Starting " + sd.getName() + " from DNA");
			ServiceManager.start(sd.getName(), cell);
		}
	}

	public static final String generateCellKeystorePWD() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static final KeyStore generateCellKeystore(Cell cell) throws NoSuchAlgorithmException,
			InvalidKeySpecException, OperatorCreationException, IOException, CertificateException, KeyStoreException {
		KeystoreManager kg = new KeystoreManager();
		KeyStore ks = kg.getKeyStore(cell.getCellName(), cell.getCellNetworkName(), cell.getCellKeystorePWD());
		return ks;
	}

	public static final void setDefaultSSLSocketFactory(Cell cell) {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		logger.info("CellManager.setDefaultSSLSocketFactory() Setting in memory keystore as default!");
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(cell.getCellKeystore());
			TrustManager[] trustAllCerts = tmf.getTrustManagers();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.setDefaultSSLSocketFactory()",
					"NoSuchAlgorithmException:");
		} catch (KeyStoreException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.setDefaultSSLSocketFactory()",
					"KeyStoreException:");
		} catch (KeyManagementException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.setDefaultSSLSocketFactory()",
					"KeyManagementException:");
		}
	}

	public static final void setRelaxedSSLSocketFactory(Cell cell) {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		logger.info("CellManager.setRelaxedSSLSocketFactory() CellName:" + cell.getCellName());
		try {
			TrustManager[] trustAllCerts = new TrustManager[1];
			trustAllCerts[0] = new RelaxedTrustManager();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.setRelaxedSSLSocketFactory()",
					"NoSuchAlgorithmException:");
		} catch (KeyManagementException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.setRelaxedSSLSocketFactory()",
					"KeyManagementException:");
		}
	}

	public static final String getCellCertificateFromKeystore(Cell cell)
			throws KeyStoreException, CertificateEncodingException, IOException {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		KeyStore ks = cell.getCellKeystore();
		logger.info("CellManager.getCellCertificateFromKeystore() CellName:" + cell.getCellName());
		Certificate cert = ks.getCertificate(cell.getCellName());
		StringWriter sw = new StringWriter();
		PemWriter pw = new PemWriter(sw);
		pw.writeObject(new JcaMiscPEMGenerator(cert));
		pw.flush();
		pw.close();
		String pemEncodedCert = sw.toString();
		logger.trace("CellManager.getCellCertificateFromKeystore() \n" + pemEncodedCert);
		return pemEncodedCert;
	}

	public static final synchronized void addCellTrustKeystore(String cellName, String certPem, Cell cell)
			throws KeyStoreException, CertificateEncodingException, IOException, CertificateException {
		Logger logger;
		logger = LoggerFactory.getLogger(CellManager.class);
		KeyStore ks = cell.getCellKeystore();
		if(ks.getCertificate(cellName)==null){
			logger.info("CellManager.addCellTrustKeystore() Adding certificate with alias: (" + cellName+")");
			logger.trace("CellManager.addCellTrustKeystore() Certificate:\n" + certPem);
			PemReader pr = new PemReader(new StringReader(certPem));
			PemObject pem = pr.readPemObject();
			pr.close();
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate cert = cf.generateCertificate(new ByteArrayInputStream(pem.getContent()));
			ks.setCertificateEntry(cellName, cert);
			CellManager.setDefaultSSLSocketFactory(cell);
		}
		else{
			logger.trace("CellManager.addCellTrustKeystore() certificate with alias: (" + cellName + ") already exists!");
		}
	}
}
