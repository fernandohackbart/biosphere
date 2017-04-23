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

import org.biosphere.tissue.blockchain.BlockException;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.TissueJoinRequest;
import org.biosphere.tissue.protocol.TissueWelcomeResponse;
import org.biosphere.tissue.protocol.TissueAnnounce;
import org.biosphere.tissue.protocol.TissueJoinResponse;
import org.biosphere.tissue.protocol.TissueWelcomeRequest;
import org.biosphere.tissue.tissue.TissueManager;
import org.bouncycastle.util.encoders.Base64;

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
			logger.debug("CellAnnounceListener.run() listening at " + (String) getParameter("AnnounceAddress") + ":"
					+ getParameter("AnnouncePort") + "!");
			socket.joinGroup(address);
			DatagramPacket packet;
			while (keepListening) {
				byte[] buf = new byte[TissueManager.joinDatagramSize];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String receivedPayload = new String(packet.getData(), 0, packet.getLength());
				logger.debug("CellAnnounceListener.run() Received request adopting cell!");
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
			TissueAnnounce tjb = mapper.readValue(tissueJoin.getBytes(), TissueAnnounce.class);

			logger.debug("CellAnnounceListener.adoptCell() Adopting: (" + tjb.getCellName() + ") "
					+ tjb.getCellNetworkName() + ":" + tjb.getTissuePort());
			try {
				CellManager.addCellTrustKeystore(tjb.getCellName(), tjb.getCellCertificate(), cell);
			} catch (CertificateEncodingException | KeyStoreException e) {
				TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
						"CellManager.addCellTrustKeystore:");
			} catch (CertificateException e) {
				TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
						"CellManager.addCellTrustKeystore:");
			}

			TissueWelcomeRequest twr = new TissueWelcomeRequest();
			twr.setTissueName(cell.getDna().getTissueName());
			twr.setCellName(cell.getCellName());
			twr.setCellCertificate(cell.getCellCertificate());
			String requestWelcome = mapper.writeValueAsString(twr);
			URL urlWelcome = new URL(
					"https://" + tjb.getCellNetworkName() + ":" + tjb.getTissuePort() + "/org/biosphere/tissue/welcome");
			logger.debug("CellAnnounceListener.adoptCell() Contacting: " + urlWelcome.getProtocol() + "://"
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

			TissueWelcomeResponse tg = mapper.readValue(responsePayload.getBytes(), TissueWelcomeResponse.class);
			logger.info("CellAnnounceListener.adoptCell() Greeting response: (" +tg.getCellName()+ ") " + tg.getMessage());

			if (tg.getMessage().equals("Greetings")) {
				
				logger.debug("CellAnnounceListener.adoptCell() Adding adopted cell to the local DNA!");
				if (cell.getDna().addCell(tg.getCellName(), tjb.getCellCertificate(), tjb.getCellNetworkName(),tjb.getTissuePort(),cell.getCellName(),cell.getChain()))
				{
					TissueJoinRequest tjreq = new TissueJoinRequest();
					tjreq.setDna(Base64.toBase64String(getCell().getDna().toJSON().getBytes()));
					tjreq.setChain(Base64.toBase64String(getCell().getChain().toJSON().getBytes()));
					String requestJoin = mapper.writeValueAsString(tjreq);
					URL urlJoin = new URL("https://" + tjb.getCellNetworkName() + ":" + tjb.getTissuePort() + "/org/biosphere/tissue/join");
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
					connJoin.connect();
					String responseJoin = getResponseAsString(connJoin.getInputStream());
					connJoin.disconnect();
					TissueJoinResponse tjr= mapper.readValue(responseJoin.getBytes(), TissueJoinResponse.class);
					logger.debug("CellAnnounceListener.adoptCell() Join response: (" +tjr.getCellName()+") "+ tjr.getMessage());
				}
				else
				{
					logger.debug("CellAnnounceListener.adoptCell() Cell ("+tg.getCellName()+") not added reverting");
					// TODO remove certificate from the keystore
				}
			}
		} catch (BlockException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
					"BlockException:");
		} catch (UnknownHostException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()",
					"UnknownHostException:");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "CellAnnounceListener.adoptCell()", "IOException:");
		}
	}

}
