package org.biosphere.tissue.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;

import java.nio.charset.StandardCharsets;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.TissueGreeting;
import org.biosphere.tissue.protocol.TissueJoin;
import org.biosphere.tissue.protocol.TissueWelcome;
import org.biosphere.tissue.tissue.TissueManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellAnnounceListener extends THREADService {
	public CellAnnounceListener() {
		super();
	}

	MulticastSocket socket = null;

	private String getResponseAsString(InputStream input) throws IOException {
		BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
		return buffer.lines().collect(Collectors.joining("\n"));
	}

	@Override
	public void interrupt() {
		super.interrupt();
		socket.close();
	}

	@Override
	public void run() {
		boolean keepListening = true;
		try {
			socket = new MulticastSocket(new Integer((String) getParameter("AnnouncePort")));
			InetAddress address = InetAddress.getByName((String) getParameter("AnnounceAddress"));
			logger.debug("CellAnnounceListener.run()", "listening at " + (String) getParameter("AnnounceAddress") + ":"
					+ getParameter("AnnouncePort") + "!");
			socket.joinGroup(address);
			DatagramPacket packet;
			while (keepListening) {
				byte[] buf = new byte[TissueManager.joinDatagramSize];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String receivedPayload = new String(packet.getData(), 0, packet.getLength());
				logger.debug("CellAnnounceListener.run()", "Received request adopting cell!");
				adoptCell(receivedPayload);
			}
			socket.leaveGroup(address);
			socket.close();
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.run()", "IOException:");
			keepListening = false;
		} catch (NullPointerException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.run()", "NullPointerException:");
			keepListening = false;
		}
	}

	private void adoptCell(String tissueJoin) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			TissueJoin tj = mapper.readValue(tissueJoin.getBytes(), TissueJoin.class);

			logger.debug("CellAnnounceListener.adoptCell()",
					"Adopting: (" + tj.getCellName() + ") " + tj.getCellNetworkName() + ":" + tj.getTissuePort());
			try {
				CellManager.addCellTrustKeystore(tj.getCellName(), tj.getCellCertificate(), cell);
			} catch (CertificateEncodingException | KeyStoreException e) {
				TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
						"CellManager.addCellTrustKeystore:");
			} catch (CertificateException e) {
				TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
						"CellManager.addCellTrustKeystore:");
			}

			TissueWelcome tw = new TissueWelcome();
			tw.setTissueName(cell.getCellDNA().getTissueName());
			tw.setCellName(cell.getCellName());
			tw.setCellCertificate(cell.getCellCertificate());
			String requestWelcome = mapper.writeValueAsString(tw);
			URL urlWelcome = new URL(
					"https://" + tj.getCellNetworkName() + ":" + tj.getTissuePort() + "/org/biosphere/tissue/welcome");
			logger.debug("CellAnnounceListener.adoptCell()", "Contacting: " + urlWelcome.getProtocol() + "://"
					+ urlWelcome.getHost() + ":" + urlWelcome.getPort() + "/org/biosphere/tissue/welcome");
			HttpsURLConnection connWelcome = (HttpsURLConnection) urlWelcome.openConnection();
			connWelcome.setRequestMethod("POST");
			connWelcome.setDoOutput(true);
			connWelcome.setInstanceFollowRedirects(false);
			connWelcome.setRequestProperty("Content-Type", "application/json");
			connWelcome.setRequestProperty("charset", "utf-8");
			connWelcome.setRequestProperty("Content-Length",
					"" + requestWelcome.getBytes(StandardCharsets.UTF_8).length);
			connWelcome.setUseCaches(false);
			DataOutputStream wrWelcome = new DataOutputStream(connWelcome.getOutputStream());
			wrWelcome.write(requestWelcome.getBytes());
			connWelcome.connect();
			String responsePayload = getResponseAsString(connWelcome.getInputStream());
			connWelcome.disconnect();

			TissueGreeting tg = mapper.readValue(responsePayload.getBytes(), TissueGreeting.class);
			logger.debug("CellAnnounceListener.adoptCell()", "Greeting response: " + tg.getMessage());
			String requestJoin = "https://" + cell.getCellNetworkName() + ":" + cell.getTissuePort()
					+ "/org/biosphere/tissue/DNA/DNACore.xml\n" + cell.getCellCertificate();
			URL urlJoin = new URL(
					"https://" + tj.getCellNetworkName() + ":" + tj.getTissuePort() + "/org/biosphere/tissue/join");
			logger.debug("CellAnnounceListener.adoptCell()", "Sending DNACore URL to: " + urlJoin.getProtocol() + "://"
					+ urlJoin.getHost() + ":" + urlJoin.getPort() + "/org/biosphere/tissue/join");
			HttpsURLConnection connJoin = (HttpsURLConnection) urlJoin.openConnection();
			connJoin.setRequestMethod("POST");
			connJoin.setDoOutput(true);
			connJoin.setInstanceFollowRedirects(false);
			connJoin.setRequestProperty("Content-Type", "application/json");
			connJoin.setRequestProperty("charset", "utf-8");
			connJoin.setRequestProperty("Content-Length", "" + requestJoin.getBytes(StandardCharsets.UTF_8).length);
			connJoin.setUseCaches(false);
			DataOutputStream wrJoin = new DataOutputStream(connJoin.getOutputStream());
			wrJoin.write(requestJoin.getBytes());
			// TODO send all the possible interfaces
			connJoin.connect();
			String responseJoin = getResponseAsString(connJoin.getInputStream());
			connJoin.disconnect();
			logger.debug("CellAnnounceListener.adoptCell()", "Join response: " + responseJoin);

			String requestChain = cell.getChain().toJSON();	
			URL urlChain = new URL("https://" + tj.getCellNetworkName() + ":" + tj.getTissuePort()
					+ "/org/biosphere/cell/chain/parse/chain");
			logger.debug("CellAnnounceListener.adoptCell()", "Sending Chain to: " + urlJoin.getProtocol() + "://"
					+ urlJoin.getHost() + ":" + urlJoin.getPort() + "/org/biosphere/cell/chain/parse/chain");
			HttpsURLConnection connChain = (HttpsURLConnection) urlChain.openConnection();
			connChain.setRequestMethod("POST");
			connChain.setDoOutput(true);
			connChain.setInstanceFollowRedirects(false);
			connChain.setRequestProperty("Content-Type", "text/plain");
			connChain.setRequestProperty("charset", "utf-8");
			connChain.setRequestProperty("Content-Length", "" + requestJoin.getBytes(StandardCharsets.UTF_8).length);
			connChain.setUseCaches(false);
			DataOutputStream wrChain = new DataOutputStream(connChain.getOutputStream());
			wrChain.write(requestChain.getBytes());
			connJoin.connect();
			String responseChain = getResponseAsString(connChain.getInputStream());
			connJoin.disconnect();
			logger.debug("CellAnnounceListener.adoptCell()", "Chain send response: " + responseChain);

			logger.debug("CellAnnounceListener.adoptCell()", "Adding adopted cell to the local DNA!");
			cell.getCellDNA().addCell(tg.getCellName(), tj.getCellCertificate(), tj.getCellNetworkName(),
					tj.getTissuePort());
		} catch (UnknownHostException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
					"UnknownHostException:");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()", "IOException:");
		}
	}

}
