package org.biosphere.tissue.blockchain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.FatBlockAppendRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainNotifyCell implements Runnable {
	public ChainNotifyCell(String hostname, int port, Block block, String remoteCellName, String localCellName,
			boolean accepted) {
		super();
		logger = LoggerFactory.getLogger(ChainNotifyCell.class);
		setHostname(hostname);
		setPort(port);
		setBlock(block);
		setLocalCellName(localCellName);
		setRemoteCellName(remoteCellName);
		setAccepted(accepted);
		setRemoteAccepted(false);
	}

	private Logger logger;
	private String hostname;
	private int port;
	private Block block;
	private boolean accepted;
	private boolean remoteAccepted;
	private String localCellName;
	private String remoteCellName;

	@Override
	public void run() {
		try {
			String peerURL = "https://" + getHostname() + ":" + getPort() + "/org/biosphere/cell/chain/append/block";
			logger.debug("ChainNotifyCell.run() Notifying " + peerURL);
			
			FatBlockAppendRequest fbar = new FatBlockAppendRequest();
			fbar.setAccepted(isAccepted());
			fbar.setNotifyingCell(getLocalCellName());
			fbar.setFlatBlock(getBlock().getFlatBlock());
			
			ObjectMapper mapper = new ObjectMapper();
			String requestNotification = mapper.writeValueAsString(fbar);
			
			URL urlNotification = new URL(peerURL);
			HttpsURLConnection connNotification = (HttpsURLConnection) urlNotification.openConnection();
			connNotification.setRequestMethod("POST");
			connNotification.setDoOutput(true);
			connNotification.setInstanceFollowRedirects(false);
			connNotification.setRequestProperty("Content-Type", "application/xml");
			connNotification.setRequestProperty("charset", "utf-8");
			connNotification.setRequestProperty("Content-Length",
					"" + requestNotification.getBytes(StandardCharsets.UTF_8).length);
			connNotification.setUseCaches(false);

			DataOutputStream wrNotification = new DataOutputStream(connNotification.getOutputStream());
			wrNotification.write(requestNotification.getBytes());
			connNotification.connect();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(connNotification.getInputStream()));
			String responsePayload = buffer.lines().collect(Collectors.joining("\n"));
			connNotification.disconnect();
			logger.debug("ChainNotifyCell.run() Notification response: " + responsePayload);
			setRemoteAccepted(Boolean.parseBoolean(responsePayload.split(":")[1]));
			logger.debug("ChainNotifyCell.run() Cell " + responsePayload.split(":")[0] + " response: " + responsePayload.split(":")[1]);
			getBlock().addVote(new Vote(getRemoteCellName(), isRemoteAccepted()));
		} catch (MalformedURLException e) {
			TissueExceptionHandler.handleGenericException(e, "ChainNotifyCell.run()", "Failed to notify tissue.");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ChainNotifyCell.run()", "Failed to notify tissue.");
		}
	}

	private final void setHostname(String hostname) {
		this.hostname = hostname;
	}

	private final String getHostname() {
		return hostname;
	}

	private final void setPort(int port) {
		this.port = port;
	}

	private final int getPort() {
		return port;
	}

	private final void setBlock(Block block) {
		this.block = block;
	}

	private final Block getBlock() {
		return block;
	}

	private final void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	private final boolean isAccepted() {
		return accepted;
	}

	private final void setLocalCellName(String cellName) {
		this.localCellName = cellName;
	}

	private final String getLocalCellName() {
		return localCellName;
	}

	private final void setRemoteAccepted(boolean remoteAccepted) {
		this.remoteAccepted = remoteAccepted;
	}

	private final boolean isRemoteAccepted() {
		return remoteAccepted;
	}

	private final void setRemoteCellName(String remoteCellName) {
		this.remoteCellName = remoteCellName;
	}

	private final String getRemoteCellName() {
		return remoteCellName;
	}
}
