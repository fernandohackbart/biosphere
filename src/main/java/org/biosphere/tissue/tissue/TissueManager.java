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

import com.fasterxml.jackson.databind.ObjectMapper;

public class TissueManager {
	public TissueManager() {
		super();
	}

	public final static int defaultTissuePort = 1040;
	public final static String announcePort = "1030";
	public final static String announceAddress = "230.0.0.1";
	public final static String joinPollInternval = "3000";
	public final static String joinMaxRetries = "2";
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
	//https://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html
	public final static String logLevelParameter = "org.slf4j.simpleLogger.defaultLogLevel";
	public final static String logLevelValue = "trace";
	public final static String logShowDateTimeParameter = "org.slf4j.simpleLogger.showDateTime";
	public final static String logShowDateTimeValue = "true";
	public final static String logOutputParameter = "org.slf4j.simpleLogger.logFile";
	public final static String logOutputValue = "System.out";
	public final static String logDateFormatParameter = "org.slf4j.simpleLogger.dateTimeFormat";
	public final static String logDateFormatValue = "yyyy-MM-dd_HH:mm:ss:SSS";
	public final static String jettLogLevelParameter = "org.eclipse.jetty.LEVEL";
	public final static String jettLogLevelValue = "ALL";
	public final static String jettLogOutputParameter = "org.eclipse.jetty.util.log.class";
	public final static String jettLogOutputValue = "org.eclipse.jetty.util.log.Slf4jLog";
	//public final static String jettLogOutputValue = "org.eclipse.jetty.util.log.StdErrLog";
	private static boolean onWelcomeProcess = false;
	

	public final static void createTissue(Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(TissueManager.class);
		logger.info("TissueManager.createTissue() Creating tissue!");
		DNA dna = new DNA();
		cell.setDna(dna);
		try {
			cell.setChain(new Chain(cell.getCellName(), cell));
		} catch (BlockException e) {
			TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.createTissue()",
					"Failed to set chain");
		}
		dna.addCell(cell.getCellName(), cell.getCellCertificate(), cell.getCellNetworkName(), cell.getTissuePort());
		cell.setTissueMember(true);
	}

	public final static void joinTissue(Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(TissueManager.class);
		int tryCount = 0;
		DatagramSocket socket = null;
		while (!cell.isTissueMember()) {
			try {
				Thread.sleep(Long.parseLong(joinPollInternval));
				TissueAnnounce tj = new TissueAnnounce();
				tj.setCellName(cell.getCellName());
				tj.setCellNetworkName(cell.getCellNetworkName());
				tj.setTissuePort(cell.getTissuePort());
				tj.setCellCertificate(cell.getCellCertificate());
				ObjectMapper mapper = new ObjectMapper();
				String tissueJoinString = mapper.writeValueAsString(tj);
				byte[] buf = (tissueJoinString).getBytes();
				logger.debug("TissueManager.joinTissue() TissueJoinString as byteArray size = "+buf.length+" bytes");
				socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				packet.setAddress(InetAddress.getByName(announceAddress));
				packet.setPort(Integer.parseInt(announcePort));
				logger.info("TissueManager.joinTissue() Announcing: (" + tj.getCellName()+") "+tj.getCellNetworkName()+":"+tj.getTissuePort());
				socket.send(packet);
				tryCount++;
				try {
					Thread.sleep(Long.parseLong(joinPollInternval));
				} catch (InterruptedException e) {
					logger.info("TissueManager.joinTissue()"+ e.getMessage());
				}
				if ((tryCount == Integer.parseInt(joinMaxRetries)) && (!cell.isTissueMember())) {
					logger.info("TissueManager.joinTissue() No tissue found to join after " + tryCount + " attempts, creating a new tissue!");
					TissueManager.createTissue(cell);
				}
			} catch (CellException e) {
				TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.joinTissue()",
						"CellException happened.");
			} catch (IOException e) {
				TissueExceptionHandler.handleGenericException(e, "TissueManager.joinTissue()",
						"Failed to open socket to joind the tissue.");
				cell.setTissueMember(false);
			} catch (InterruptedException e) {
				TissueExceptionHandler.handleGenericException(e, "TissueManager.joinTissue()",
						"Failed to open socket to joind the tissue.");
			}
		}
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
