package org.biosphere.tissue.blockchain;

import java.io.DataOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.BlockAppendRequest;
import org.biosphere.tissue.protocol.BlockAppendResponse;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainNotifier implements Runnable {
	public ChainNotifier(String hostname, int port, Block block, String remoteCellName, String localCellName,
			boolean accepted,boolean ensureAcceptance) {
		super();
		logger = LoggerFactory.getLogger(ChainNotifier.class);
		setHostname(hostname);
		setPort(port);
		setBlock(block);
		setLocalCellName(localCellName);
		setRemoteCellName(remoteCellName);
		setAccepted(accepted);
		setEnsureAcceptance(ensureAcceptance);
		setRemoteAccepted(false);
	}

	private Logger logger;
	private String hostname;
	private int port;
	private Block block;
	private boolean accepted;
	private boolean remoteAccepted;
	private boolean ensureAcceptance;
	private String localCellName;
	private String remoteCellName;

	@Override
	public void run() {
		try {
			String peerURL = "https://" + getHostname() + ":" + getPort() + TissueManager.TissueChainAppendBlockURI;
			logger.debug("ChainNotifier.run() Notifying " + peerURL +" block ("+ getBlock().getBlockID()+") TITLE("+getBlock().getTitle()+")");
			logger.trace("ChainNotifier.run() Block votes in the flat block: " + getBlock().getFlatBlock().getAcceptanceVotes().size());
			
			BlockAppendRequest fbar = new BlockAppendRequest();
			fbar.setAccepted(isAccepted());
			fbar.setNotifyingCell(getLocalCellName());
			fbar.setFlatBlock(getBlock().getFlatBlock());
			fbar.setEnsureAcceptance(ensureAcceptance);
			
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
			
			String responsePayload ="ERROR";
			try{
				connNotification.connect();
				responsePayload = RequestUtils.getRequestAsString(connNotification.getInputStream());	
				connNotification.disconnect();
				BlockAppendResponse fbr = mapper.readValue(responsePayload.getBytes(),BlockAppendResponse.class);
				logger.debug("ChainNotifier.run() Notification response: cell (" + fbr.getCellName()+ ") = " + fbr.isAccepted());
				setRemoteAccepted(fbr.isAccepted());
				getBlock().addVote(new Vote(getRemoteCellName(), isRemoteAccepted()));
			}
			catch (IOException e)
			{
				logger.debug("ChainNotifier.run() Notification response: IOException (" + e.getLocalizedMessage()+ ")");
			}
		} catch (MalformedURLException e) {
			TissueExceptionHandler.handleGenericException(e, "ChainNotifier.run()", "Failed to notify tissue.");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ChainNotifier.run()", "Failed to notify tissue.");
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

	public final boolean isEnsureAcceptance() {
		return ensureAcceptance;
	}

	public final void setEnsureAcceptance(boolean ensureAcceptance) {
		this.ensureAcceptance = ensureAcceptance;
	}
}
