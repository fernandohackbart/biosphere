package org.biosphere.tissue.tissue;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.DNACore;
import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;

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
	public final static int defaultSerialNumber = 1;
	public final static Long monitorInterval = 240000L;
	public final static int portJumpFactor = 20;

	public final static void createTissue(Cell cell) throws CellException {
		Logger logger = new Logger();
		logger.info("TissueManager.createTissue()", " Creating tissue!");
		DNACore dna = new DNACore();
		cell.setCellDNA(dna);
		try {
			cell.setChain(new Chain(cell.getCellName(), cell));
		} catch (BlockException e) {
			TissueExceptionHandler.handleUnrecoverableGenericException(e, "TissueManager.createTissue()",
					"Failed to set chain");
		}
		dna.incept();
		dna.addCell(cell.getCellName(), cell.getCellCertificate(), cell.getCellNetworkName(), cell.getTissuePort());
		cell.setTissueMember(true);
	}

	public final static void joinTissue(Cell cell) throws CellException {
		Logger logger = new Logger();
		int tryCount = 0;
		DatagramSocket socket = null;
		while (!cell.isTissueMember()) {
			try {
				Thread.sleep(Long.parseLong(joinPollInternval));
				byte[] buf = new byte[256];
				String myAddress = cell.getCellNetworkName() + ":" + cell.getTissuePort();
				buf = (myAddress).getBytes();
				socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				packet.setAddress(InetAddress.getByName(announceAddress));
				packet.setPort(Integer.parseInt(announcePort));
				logger.info("TissueManager.joinTissue()", " Announcing: " + myAddress);
				socket.send(packet);
				tryCount++;
				try {
					Thread.sleep(Long.parseLong(joinPollInternval));
				} catch (InterruptedException e) {
					logger.info("TissueManager.joinTissue()", e.getMessage());
				}
				if ((tryCount == Integer.parseInt(joinMaxRetries)) && (!cell.isTissueMember())) {
					logger.info("TissueManager.joinTissue()",
							" No tissue found to join after " + tryCount + " attempts, creating a new tissue!");
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
		Logger logger = new Logger();
		String tissueName = generateRandomTissueName();
		// String tissueName = generateDateTissueName();
		logger.info("TissueManager.generateTissueName()", "Tissue name: " + tissueName);
		return tissueName;
	}

	private static String generateRandomTissueName() {
		String tissueName = "Biosphere-" + UUID.randomUUID().toString();
		return tissueName;
	}

	private static String generateDateTissueName() {
		String tissueName = "NotDefined!";
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		tissueName = "Biosphere-" + strDate;
		return tissueName;
	}
}
