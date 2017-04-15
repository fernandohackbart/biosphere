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

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.Tissue;
import org.biosphere.tissue.DNA.Tissueservicetype;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.services.ServiceDefinition;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.services.ServletHandlerDefinition;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.KeystoreManager;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RelaxedTrustManager;

import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

public class CellManager {
	public CellManager() {
		super();
	}

	public final static void setupCell(Cell cell) {
		cell.setCellName(generateCellName());
		cell.setCellNetworkName(getCellNetworkName());
		cell.setTissueMember(false);
		cell.setTissuePort(TissueManager.defaultTissuePort);
		cell.setCellKeystorePWD(generateCellKeystorePWD());
	}

	private static String generateCellName() {
		return generateCellRandomName();
		// return generateCellNetName();
	}

	private static String generateCellRandomName() {
		Logger logger = new Logger();
		String cellName = UUID.randomUUID().toString();
		logger.info("CellManager.generateCellName()", "Cell name: " + cellName);
		return cellName;
	}

	private static String generateCellNetName() {
		Logger logger = new Logger();
		String cellName = "NotDefined!";
		try {
			String networkName = InetAddress.getLocalHost().getHostName();
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			String strDate = sdfDate.format(now);
			cellName = networkName + "-" + strDate;
			logger.info("CellManager.generateCellName()", "Cell name: " + cellName);
		} catch (UnknownHostException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.generateCellName()",
					"Failed to generate Cell name due to UnknowHost Exception!");
		}
		return cellName;
	}

	public static String getCellNetworkName() {
		Logger logger = new Logger();
		String cellName = "localhost";
		try {
			String networkName = InetAddress.getLocalHost().getHostName();
			cellName = networkName;
			logger.info("CellManager.generateCellName()", "Cell network name: " + cellName);
		} catch (UnknownHostException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.gatCellNetworkName()",
					"Failed to generate Cell name due to UnknowHost Exception!");
		}
		return cellName;
	}

	public final static void stopCell() {
		try {
			ServiceManager.stop("THREAD", "CellMonitor");
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.stopCell()", "Failed to stop Cell.");
		}
	}

	public final static ServiceDefinition getCellMonitorDefinition() {
		ServiceDefinition sdCellMonitor = new ServiceDefinition();
		sdCellMonitor.setServiceDefinitionName("CellMonitor");
		sdCellMonitor.setServiceDefinitionType("THREAD");
		sdCellMonitor.setServiceDefinitionVersion("0.1");
		sdCellMonitor.setServiceDefinitionDaemon(false);
		sdCellMonitor.setServiceDefinitionClass("org.biosphere.tissue.services.CellMonitor");
		sdCellMonitor.addServiceDefinitionParameter("Interval", TissueManager.monitorInterval);
		return sdCellMonitor;
	}

	public final static ServiceDefinition getCellAnnounceListenerDefinition() {
		ServiceDefinition sdCellAnnounceListener = new ServiceDefinition();
		sdCellAnnounceListener.setServiceDefinitionName("CellAnnounceListener");
		sdCellAnnounceListener.setServiceDefinitionType("THREAD");
		sdCellAnnounceListener.setServiceDefinitionVersion("0.1");
		sdCellAnnounceListener.setServiceDefinitionClass("org.biosphere.tissue.services.CellAnnounceListener");
		sdCellAnnounceListener.addServiceDefinitionParameter("AnnouncePort", TissueManager.announcePort);
		sdCellAnnounceListener.addServiceDefinitionParameter("AnnounceAddress", TissueManager.announceAddress);
		return sdCellAnnounceListener;
	}

	public final static ServiceDefinition getCellACSDefinition() {
		ServiceDefinition sdCellACS = new ServiceDefinition();
		sdCellACS.setServiceDefinitionName("CellAdministrationConsole");
		sdCellACS.setServiceDefinitionType("THREAD");
		sdCellACS.setServiceDefinitionVersion("0.1");
		sdCellACS.setServiceDefinitionClass("org.biosphere.tissue.services.CellAdministrationConsole");
		sdCellACS.addServiceDefinitionParameter("ListenPort", TissueManager.announcePort);
		return sdCellACS;
	}
	
	public final static ServiceDefinition getCellTissueServletListenerDefinition() {
		ArrayList<ServletHandlerDefinition> cellTissueJettyListenerHandlers = new ArrayList<ServletHandlerDefinition>();
		
		ServletHandlerDefinition cellDNACoreSHD = new ServletHandlerDefinition();
		cellDNACoreSHD.setClassName("org.biosphere.tissue.handlers.CellDNACoreHandler");
		cellDNACoreSHD.setContentType("application/xml");
		ArrayList<String> cellDNACoreContexts = new ArrayList<String>();
		cellDNACoreContexts.add("/org/biosphere/tissue/DNA/DNACore.xml");
		cellDNACoreSHD.setContexts(cellDNACoreContexts);
		cellTissueJettyListenerHandlers.add(cellDNACoreSHD);
		
		ServletHandlerDefinition cellTissueWelcomeSHD = new ServletHandlerDefinition();
		cellTissueWelcomeSHD.setClassName("org.biosphere.tissue.handlers.CellTissueWelcomeHandler");
		cellTissueWelcomeSHD.setContentType("text/plain");
		ArrayList<String> cellTissueWelcomeContexts = new ArrayList<String>();
		cellTissueWelcomeContexts.add("/org/biosphere/tissue/welcome");
		cellTissueWelcomeSHD.setContexts(cellTissueWelcomeContexts);
		cellTissueJettyListenerHandlers.add(cellTissueWelcomeSHD);
		
		ServletHandlerDefinition cellTissueJoinSHD = new ServletHandlerDefinition();
		cellTissueJoinSHD.setClassName("org.biosphere.tissue.handlers.CellTissueJoinHandler");
		cellTissueJoinSHD.setContentType("text/plain");
		ArrayList<String> cellTissueJoinContexts = new ArrayList<String>();
		cellTissueJoinContexts.add("/org/biosphere/tissue/join");
		cellTissueJoinSHD.setContexts(cellTissueJoinContexts);
		cellTissueJettyListenerHandlers.add(cellTissueJoinSHD);

		ServletHandlerDefinition cellDNASchemaSHD = new ServletHandlerDefinition();
		cellDNASchemaSHD.setClassName("org.biosphere.tissue.handlers.CellDNASchemaHandler");
		cellDNASchemaSHD.setContentType("application/xml");	
		ArrayList<String> cellDNASchemaContexts = new ArrayList<String>();
		cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueCell-1.0.xsd");
		cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueCellInterface-1.0.xsd");
		cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueDNA-1.0.xsd");
		cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueService-1.0.xsd");
		cellDNASchemaSHD.setContexts(cellDNASchemaContexts);
		cellTissueJettyListenerHandlers.add(cellDNASchemaSHD);
		
		ServletHandlerDefinition cellStopSHD = new ServletHandlerDefinition();
		cellStopSHD.setClassName("org.biosphere.tissue.handlers.CellStopHandler");
		cellStopSHD.setContentType("text/plain");
		ArrayList<String> cellStopContexts = new ArrayList<String>();
		cellStopContexts.add("/org/biosphere/cell/stop");
		cellStopSHD.setContexts(cellStopContexts);
		cellTissueJettyListenerHandlers.add(cellStopSHD);
		
		ServletHandlerDefinition cellHTTPContextAddSHD = new ServletHandlerDefinition();
		cellHTTPContextAddSHD.setClassName("org.biosphere.tissue.handlers.CellHTTPContextManagerHandler");
		cellHTTPContextAddSHD.setContentType("text/plain");
		ArrayList<String> cellHTTPContextAddContexts = new ArrayList<String>();
		cellHTTPContextAddContexts.add("/org/biosphere/cell/http/context/add");
		cellHTTPContextAddSHD.setContexts(cellHTTPContextAddContexts);
		cellTissueJettyListenerHandlers.add(cellHTTPContextAddSHD);		
		
		ServletHandlerDefinition serviceStopSHD = new ServletHandlerDefinition();
		serviceStopSHD.setClassName("org.biosphere.tissue.handlers.ServiceStopHandler");
		serviceStopSHD.setContentType("text/html");
		ArrayList<String> serviceStopContexts = new ArrayList<String>();
		serviceStopContexts.add("/org/biosphere/cell/service/stop");
		serviceStopSHD.setContexts(serviceStopContexts);
		cellTissueJettyListenerHandlers.add(serviceStopSHD);
		
		ServletHandlerDefinition httpServiceStopSHD = new ServletHandlerDefinition();
		httpServiceStopSHD.setClassName("org.biosphere.tissue.handlers.HTTPServiceStopHandler");
		httpServiceStopSHD.setContentType("text/html");
		ArrayList<String> httpServiceStopContexts = new ArrayList<String>();
		httpServiceStopContexts.add("/org/biosphere/cell/httpservice/stop");
		httpServiceStopSHD.setContexts(httpServiceStopContexts);
		cellTissueJettyListenerHandlers.add(httpServiceStopSHD);

		ServletHandlerDefinition cellStatusSHD = new ServletHandlerDefinition();
		cellStatusSHD.setClassName("org.biosphere.tissue.handlers.CellStatusHandler");
		cellStatusSHD.setContentType("text/plain");
		ArrayList<String> cellStatusContexts = new ArrayList<String>();
		cellStatusContexts.add("/org/biosphere/cell/status");
		cellStatusSHD.setContexts(cellStatusContexts);
		cellTissueJettyListenerHandlers.add(cellStatusSHD);

		ServletHandlerDefinition chainAddBlockSHD = new ServletHandlerDefinition();
		chainAddBlockSHD.setClassName("org.biosphere.tissue.handlers.ChainAddBlockHandler");
		chainAddBlockSHD.setContentType("text/plain");
		ArrayList<String> chainAddBlockContexts = new ArrayList<String>();
		chainAddBlockContexts.add("/org/biosphere/cell/chain/add/block");
		chainAddBlockSHD.setContexts(chainAddBlockContexts);
		cellTissueJettyListenerHandlers.add(chainAddBlockSHD);

		ServletHandlerDefinition chainAppendBlockSHD = new ServletHandlerDefinition();
		chainAppendBlockSHD.setClassName("org.biosphere.tissue.handlers.ChainAppendBlockHandler");
		chainAppendBlockSHD.setContentType("text/plain");
		ArrayList<String> chainAppendBlockContexts = new ArrayList<String>();
		chainAppendBlockContexts.add("/org/biosphere/cell/chain/append/block");
		chainAppendBlockSHD.setContexts(chainAppendBlockContexts);
		cellTissueJettyListenerHandlers.add(chainAppendBlockSHD);
		
		ServletHandlerDefinition chainParseChainSHD = new ServletHandlerDefinition();
		chainParseChainSHD.setClassName("org.biosphere.tissue.handlers.ChainParseChainHandler");
		chainParseChainSHD.setContentType("text/plain");
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		chainParseChainContexts.add("/org/biosphere/cell/chain/parse/chain");
		chainParseChainSHD.setContexts(chainParseChainContexts);
		cellTissueJettyListenerHandlers.add(chainParseChainSHD);
		
		ServletHandlerDefinition chainGetChainSHD = new ServletHandlerDefinition();
		chainGetChainSHD.setClassName("org.biosphere.tissue.handlers.ChainGetFlatChainHandler");
		chainGetChainSHD.setContentType("text/plain");
		ArrayList<String> chainGetChainContexts = new ArrayList<String>();
		chainGetChainContexts.add("/org/biosphere/cell/chain/get/chain");
		chainGetChainSHD.setContexts(chainGetChainContexts);
		cellTissueJettyListenerHandlers.add(chainGetChainSHD);
		
		ServletHandlerDefinition chainGetImageChainSHD = new ServletHandlerDefinition();
		chainGetImageChainSHD.setClassName("org.biosphere.tissue.handlers.ChainGetImageChainHandler");
		chainGetImageChainSHD.setContentType("image/png");
		ArrayList<String> chainGetImageChainContexts = new ArrayList<String>();
		chainGetImageChainContexts.add("/org/biosphere/cell/chain/get/chainimage");
		chainGetImageChainSHD.setContexts(chainGetImageChainContexts);
		cellTissueJettyListenerHandlers.add(chainGetImageChainSHD);
		//#################################################################################################
		
		ServiceDefinition sdCellTissueJettyListener = new ServiceDefinition();
		sdCellTissueJettyListener.setServiceDefinitionName("CellTissueJettyListener");
		sdCellTissueJettyListener.setServiceDefinitionType("SERVLET");
		sdCellTissueJettyListener.setServiceDefinitionVersion("0.1");
		sdCellTissueJettyListener.setServiceDefinitionClass("org.eclipse.jetty.server.Server");
		sdCellTissueJettyListener.addServiceDefinitionParameter("Handlers", cellTissueJettyListenerHandlers);
		sdCellTissueJettyListener.addServiceDefinitionParameter("DefaultHTTPPort", TissueManager.defaultTissuePort);
		return sdCellTissueJettyListener;
	}
	
	public final static ServiceDefinition getCellServiceListenerDefinition() {
		// all those parameters should be placed inside the DNA and the consumed
		// from there when starting the service
		
		ArrayList<ServletHandlerDefinition> cellServiceListenerHandlers = new ArrayList<ServletHandlerDefinition>();
		ServletHandlerDefinition cellServiceListenerSHD = new ServletHandlerDefinition();
		cellServiceListenerSHD.setClassName("org.biosphere.tissue.handlers.CellServiceInstantiationHandler");
		cellServiceListenerSHD.setContentType("text/plain");
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		chainParseChainContexts.add("/");
		cellServiceListenerSHD.setContexts(chainParseChainContexts);
		cellServiceListenerHandlers.add(cellServiceListenerSHD);

		ServiceDefinition sdCellTissueListener = new ServiceDefinition();
		sdCellTissueListener.setServiceDefinitionName("CellServiceListener");
		sdCellTissueListener.setServiceDefinitionType("SERVLET");
		sdCellTissueListener.setServiceDefinitionVersion("0.1");
		sdCellTissueListener.setServiceDefinitionClass("org.eclipse.jetty.server.Server");
		sdCellTissueListener.addServiceDefinitionParameter("Handlers", cellServiceListenerHandlers);
		sdCellTissueListener.addServiceDefinitionParameter("DefaultHTTPPort",
				TissueManager.defaultTissuePort + TissueManager.portJumpFactor);
		return sdCellTissueListener;
	}

	public static final void startTissueListenerService(Cell cell) throws CellException {
		cell.setTissuePort(ServiceManager.startServiceDefinition(getCellTissueServletListenerDefinition(), cell));
	}

	public static final void loadServicesDNA(Cell cell) {
		Logger logger = new Logger();
		logger.debug("CellManager.addServicesDNA()", "Adding CellMonitor definition to DNA");
		cell.getCellDNA().addService(getCellMonitorDefinition());
		logger.debug("CellManager.addServicesDNA()", "Adding CellAnnounceListener definition to DNA");
		cell.getCellDNA().addService(getCellAnnounceListenerDefinition());
		logger.debug("CellManager.addServicesDNA()", "Adding CellTissueListener definition to DNA");
		cell.getCellDNA().addService(getCellTissueServletListenerDefinition());
		// logger.debug("CellManager.addServicesDNA()","Adding CellACS
		// definition to DNA");
		// cell.getCellDNA().addService(getCellACSDefinition());
		logger.debug("CellManager.addServicesDNA()", "Adding CellServiceListener definition to DNA");
		cell.getCellDNA().addService(getCellServiceListenerDefinition());
	}

	public static final void startServicesDNA(Cell cell) {
		Logger logger = new Logger();
		Tissue.Tissueservices services = cell.getCellDNA().getServices();
		Iterator<Tissueservicetype> servicesIterator = services.getTissueservice().iterator();
		while (servicesIterator.hasNext()) {
			Tissueservicetype service = (Tissueservicetype) servicesIterator.next();
			logger.debug("CellManager.startServicesDNA()", "Starting " + service.getServicename() + " from DNA");
			ServiceManager.start(service.getServicename(), cell);
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
		Logger logger = new Logger();
		logger.info("CellManager.setDefaultSSLSocketFactory()", "CellName:" + cell.getCellName());
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
		Logger logger = new Logger();
		logger.info("CellManager.setRelaxedSSLSocketFactory()", "CellName:" + cell.getCellName());
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
		Logger logger = new Logger();
		KeyStore ks = cell.getCellKeystore();
		logger.info("CellManager.getCellCertificateFromKeystore()", "CellName:" + cell.getCellName());
		Certificate cert = ks.getCertificate(cell.getCellName());
		StringWriter sw = new StringWriter();
		PemWriter pw = new PemWriter(sw);
		pw.writeObject(new JcaMiscPEMGenerator(cert));
		pw.flush();
		pw.close();
		String pemEncodedCert = sw.toString();
		logger.debug("CellManager.getCellCertificateFromKeystore()", "\n" + pemEncodedCert);
		return pemEncodedCert;
	}

	public static final synchronized void addCellTrustKeystore(String cellName, String certPem, Cell cell)
			throws KeyStoreException, CertificateEncodingException, IOException, CertificateException {
		Logger logger = new Logger();
		logger.info("CellManager.addCellTrustKeystore()",
				"CellName:" + cell.getCellName() + " remote CellName:" + cellName);
		logger.info("CellManager.addCellTrustKeystore()", "Certificate:\n" + certPem);
		PemReader pr = new PemReader(new StringReader(certPem));
		PemObject pem = pr.readPemObject();
		pr.close();
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		Certificate cert = cf.generateCertificate(new ByteArrayInputStream(pem.getContent()));
		KeyStore ks = cell.getCellKeystore();
		ks.setCertificateEntry(cellName, cert);
		CellManager.setDefaultSSLSocketFactory(cell);
	}
}
