package org.biosphere.tissue.services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.ServiceDiscoveryRequest;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceDiscoverer implements Runnable {

	public ServiceDiscoverer(CellInterface cellInterface, ServiceDiscoveryRequest sdr, String notHandledRequestID,String remoteCellName,Cell localCell) {
		super();
		logger = LoggerFactory.getLogger(ServiceDiscoverer.class);
		setCellInterface(cellInterface);
		setServiceDiscoveryRequest(sdr);
		setLocalCell(localCell);
		setRemoteCellName(remoteCellName);
		setNotHandledRequestID(notHandledRequestID);
	}

	private Logger logger;
	private CellInterface cellInterface;
	private ServiceDiscoveryRequest serviceDiscoveryRequest;
	private String notHandledRequestID;
	private Cell localCell;
	private String remoteCellName;

	public final CellInterface getCellInterface() {
		return cellInterface;
	}

	public final void setCellInterface(CellInterface cellInterface) {
		this.cellInterface = cellInterface;
	}

	public final ServiceDiscoveryRequest getServiceDiscoveryRequest() {
		return serviceDiscoveryRequest;
	}

	public final void setServiceDiscoveryRequest(ServiceDiscoveryRequest serviceDiscoveryRequest) {
		this.serviceDiscoveryRequest = serviceDiscoveryRequest;
	}

	@Override
	public void run() {
		try {
			String peerURL = "https://" + getCellInterface().getCellNetworkName() + ":" + getCellInterface().getPort() + TissueManager.TissueServiceDiscoverURI;
			logger.debug("ServiceDiscoverer.run(" + getNotHandledRequestID() + ") Notifying " + peerURL + " service (" + getServiceDiscoveryRequest().getServiceName() + ") RequestID (" + getServiceDiscoveryRequest().getRequestID() + ")");

			ObjectMapper mapper = new ObjectMapper();
			String requestServiceDiscovery = mapper.writeValueAsString(getServiceDiscoveryRequest());

			URL urlServiceDiscovery = new URL(peerURL);
			HttpsURLConnection connServiceDiscovery = (HttpsURLConnection) urlServiceDiscovery.openConnection();
			connServiceDiscovery.setRequestMethod("POST");
			connServiceDiscovery.setDoOutput(true);
			connServiceDiscovery.setInstanceFollowRedirects(false);
			connServiceDiscovery.setRequestProperty("Content-Type", "application/json");
			connServiceDiscovery.setRequestProperty("charset", "utf-8");
			connServiceDiscovery.setRequestProperty("Content-Length", "" + requestServiceDiscovery.getBytes(StandardCharsets.UTF_8).length);
			connServiceDiscovery.setUseCaches(false);
			
			
			try {
				DataOutputStream wrNotification = new DataOutputStream(connServiceDiscovery.getOutputStream());
				wrNotification.write(requestServiceDiscovery.getBytes());
			} catch (ConnectException e) {
				// TODO mark the cell as possibly dead for the removal
				getLocalCell().getDna().incrementContactFailures(getRemoteCellName());
				logger.error("ChainNotifier.run() Notification response: ConnectException (" + e.getLocalizedMessage() + ") marking cell ("+getRemoteCellName()+") as possibly dead!");
			}
			

			String responsePayload = "ERROR";
			try {
				connServiceDiscovery.connect();
				responsePayload = RequestUtils.getRequestAsString(connServiceDiscovery.getInputStream());
				connServiceDiscovery.disconnect();
				ServiceDiscoveryResponse sdresp = mapper.readValue(responsePayload.getBytes(), ServiceDiscoveryResponse.class);
				logger.debug("ServiceDiscoverer.run(" + getNotHandledRequestID() + ") Notification response: cell (" + sdresp.getCellName() + ") = " + sdresp.isRunning());

				if (sdresp.isRunning()) {
					logger.debug("ServiceDiscoverer.run(" + getNotHandledRequestID() + ") Adding discovery response from cell (" + sdresp.getCellName() + ") = " + sdresp.isRunning());
					ServiceManager.addDiscoveryResponse(sdresp);
				}
			} catch (IOException e) {
				logger.error("ChainNotifier.run() Notification response: IOException (" + e.getLocalizedMessage() + ")");
			}
		} catch (MalformedURLException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoverer.run(" + getNotHandledRequestID() + ")", "Failed to notify cell.");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoverer.run(" + getNotHandledRequestID() + ")", "Failed to notify cell.");
		}
	}

	public final String getNotHandledRequestID() {
		return notHandledRequestID;
	}

	public final void setNotHandledRequestID(String notHandledRequestID) {
		this.notHandledRequestID = notHandledRequestID;
	}

	public final Cell getLocalCell() {
		return localCell;
	}

	public final void setLocalCell(Cell localCell) {
		this.localCell = localCell;
	}

	public final String getRemoteCellName() {
		return remoteCellName;
	}

	public final void setRemoteCellName(String remoteCellName) {
		this.remoteCellName = remoteCellName;
	}

}
