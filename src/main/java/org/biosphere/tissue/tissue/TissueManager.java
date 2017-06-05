package org.biosphere.tissue.tissue;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.UUID;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.DNA;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.TissueAnnounce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TissueManager {
	public TissueManager() {
		super();
	}

	public final static String tissueVersion = "1.0";
	public final static int defaultTissuePort = 1040;
	public final static String announcePort = "1030";
	public final static String announceAddress = "230.0.0.1";
	public final static String joinPollInternval = "7000";
	public final static int joinMaxRetries = 3;
	public final static String OUDN = ", OU=IT, O=Familj, L=Lomma, ST=Skane, C=SE";
	public final static String SignerBuilderName = "SHA256withRSA";
	public final static int keyStrenght = 2048;
	public final static String keyBigInteger = "35";
	public final static int validityCA = 10;
	public final static int validity = 1;
	public final static int joinDatagramSize = 2048;
	public final static int defaultSerialNumber = 1;
	public final static Long monitorInterval = 240000L;
	public final static int portJumpFactor = 20;
	// https://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html
	public final static String logLevelParameter = "org.slf4j.simpleLogger.defaultLogLevel";
	public final static String logLevelValue = "trace";
	public final static String logShowDateTimeParameter = "org.slf4j.simpleLogger.showDateTime";
	public final static String logShowDateTimeValue = "true";
	public final static String logOutputParameter = "org.slf4j.simpleLogger.logFile";
	public final static String logOutputValue = "System.out";
	public final static String logDateFormatParameter = "org.slf4j.simpleLogger.dateTimeFormat";
	public final static String logDateFormatValue = "yyyy-MM-dd_HH:mm:ss:SSS";
	public final static String jettyLogLevelParameter = "org.eclipse.jetty.LEVEL";
	public final static String jettyLogLevelValue = "ALL";
	public final static String jettyLogOutputParameter = "org.eclipse.jetty.util.log.class";
	public final static String jettyLogOutputValue = "org.eclipse.jetty.util.log.Slf4jLog";
	public final static int jettyMaxThreadPoolSize = 50;
	public final static int jettyOutputBufferSize = 32768;
	public final static int jettyRequestHeaderSize = 8192;
	public final static int jettyResponseHeaderSize = 8192;
	public final static boolean jettySendServerVersion = true;
	public final static boolean jettySendDateHeader = false;
	public final static long serviceDiscoveryTimeout = 3000L;
	public final static long serviceDiscoveryInterval = 500L;
	public final static String defaultContentType = "application/json";
	public final static String defaultContentEncoding = "utf-8";

	// public final static String jettLogOutputValue = "org.eclipse.jetty.util.log.StdErrLog";
	private static boolean onWelcomeProcess = false;
	public final static long acceptanceTimeout = 5500L;
	public final static long acceptanceInterval = 1000L;
	public final static String ThreadServiceClass = "org.biosphere.tissue.services.THREADService";
	public final static String ServletServiceClass = "org.eclipse.jetty.server.Server";

	public final static String TissueCellAddOperation = "CellAdd";
	public final static String TissueCellRemoveOperation = "CellRemove";
	public final static String TissueServiceEnableOperation = "ServiceEnable";
	public final static String TissueServicePortParameter = "ServicePort";
	public final static String TissueServiceDiscoverClass = "org.biosphere.tissue.handlers.ServiceDiscoveryHandler";
	public final static String TissueServiceDiscoverURI = "/org/biosphere/cell/service/discover";
	public final static String TissueWelcomeClass = "org.biosphere.tissue.handlers.CellTissueWelcomeHandler";
	public final static String TissueWelcomeURI = "/org/biosphere/tissue/welcome";
	public final static String TissueJoinClass = "org.biosphere.tissue.handlers.CellTissueJoinHandler";
	public final static String TissueJoinURI = "/org/biosphere/tissue/join";
	public final static String TissueChainAddBlockClass = "org.biosphere.tissue.handlers.ChainAddBlockHandler";
	public final static String TissueChainAddBlockURI = "/org/biosphere/cell/chain/add/block";
	public final static String TissueChainAppendBlockClass = "org.biosphere.tissue.handlers.ChainAppendBlockHandler";
	public final static String TissueChainAppendBlockURI = "/org/biosphere/cell/chain/append/block";

	public final static void createTissue(Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(TissueManager.class);
		logger.info("TissueManager.createTissue() Creating tissue!");
		DNA dna = new DNA();
		cell.setDna(dna);
		try {
			cell.setChain(new Chain(cell.getCellName(), cell));
		} catch (BlockException e) {
			TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.createTissue()", "Failed to set chain");
		}
		try {
			dna.addCell(cell.getCellName(), cell.getCellCertificate(), cell.getCellNetworkName(), cell.getTissuePort(), cell.getCellName(), cell);
		} catch (JsonProcessingException | BlockException e) {
			TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.createTissue()", "Failed to set chain");
		}
		cell.setTissueMember(true);
	}

	public final static void joinTissue(Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(TissueManager.class);
		int tryCount = 0;
		DatagramSocket socket = null;
		logger.trace("TissueManager.joinTissue() Entering!");
		while (!cell.isTissueMember()) {
			try {
				if (!TissueManager.isOnWelcomeProcess()) {
					logger.debug("TissueManager.joinTissue() Starting new announcing broadcast! ");
					try {
						TissueAnnounce tj = new TissueAnnounce();
						tj.setCellName(cell.getCellName());
						tj.setCellNetworkName(cell.getCellNetworkName());
						tj.setTissuePort(cell.getTissuePort());
						tj.setCellCertificate(cell.getCellCertificate());
						ObjectMapper mapper = new ObjectMapper();
						String tissueJoinString = mapper.writeValueAsString(tj);
						byte[] buf = (tissueJoinString).getBytes();
						logger.trace("TissueManager.joinTissue() TissueJoinString as byteArray size " + buf.length + " bytes");
						logger.trace("TissueManager.joinTissue() Request ID (" + tj.getRequestID() + ")");
						socket = new DatagramSocket();
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						packet.setAddress(InetAddress.getByName(announceAddress));
						packet.setPort(Integer.parseInt(announcePort));
						logger.info("TissueManager.joinTissue() Announcing: (" + tj.getCellName() + ") " + tj.getCellNetworkName() + ":" + tj.getTissuePort());
						socket.send(packet);
					} catch (IOException e) {
						TissueExceptionHandler.handleGenericException(e, "TissueManager.joinTissue()", "Failed to open socket to joind the tissue.");
						cell.setTissueMember(false);
					}
				} else {
					logger.debug("TissueManager.joinTissue() Skipping new request as the TissueManager.isOnWelcomeProcess()=true!");
				}

				if ((tryCount == joinMaxRetries) && (!cell.isTissueMember())) {
					logger.warn("TissueManager.joinTissue() No tissue found to join after " + tryCount + " attempts, creating a new tissue!");
					TissueManager.createTissue(cell);
				}
				logger.warn("TissueManager.joinTissue() Going to sleep for " + joinPollInternval + " miliseconds!");
				Thread.sleep(Long.parseLong(joinPollInternval));
				tryCount++;
				logger.trace("TissueManager.joinTissue() Try count = " + tryCount);

			} catch (InterruptedException e) {
				TissueExceptionHandler.handleGenericException(e, "TissueManager.joinTissue()", "Failed to open socket to joind the tissue.");
			} catch (CellException e) {
				TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.joinTissue()", "CellException happened.");
			}
		}
		logger.trace("TissueManager.joinTissue() Exiting!");
		socket.close();

	}

	public static String generateTissueName() {
		Logger logger = LoggerFactory.getLogger(TissueManager.class);
		String tissueName = generateRandomTissueName();
		logger.info("TissueManager.generateTissueName() Tissue name: " + tissueName);
		return tissueName;
	}

	private static String generateRandomTissueName() {
		String tissueName = "Biosphere-" + UUID.randomUUID().toString();
		return tissueName;
	}

	public static final boolean isOnWelcomeProcess() {
		return onWelcomeProcess;
	}

	public static synchronized final void setOnWelcomeProcess(boolean onWelcomeProcess) {
		TissueManager.onWelcomeProcess = onWelcomeProcess;
	}
}
