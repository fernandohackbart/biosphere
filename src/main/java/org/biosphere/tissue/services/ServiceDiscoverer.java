package org.biosphere.tissue.services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.blockchain.ChainNotifier;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.ServiceDiscoveryRequest;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
import org.biosphere.tissue.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceDiscoverer implements Runnable {
	
	public ServiceDiscoverer(CellInterface cellInterface,ServiceDiscoveryRequest sdr) {
		super();
		logger = LoggerFactory.getLogger(ChainNotifier.class);
		setCellInterface(cellInterface);
		setServiceDiscoveryRequest(sdr);
	}
	
	private Logger logger;
	private CellInterface cellInterface; 
	private ServiceDiscoveryRequest serviceDiscoveryRequest;
	
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
			String peerURL = "https://" + getCellInterface().getCellNetworkName() + ":" + getCellInterface().getPort() + "/org/biosphere/cell/service/discover";
			logger.debug("ServiceDiscoverer.run() Notifying " + peerURL +" service ("+ getServiceDiscoveryRequest().getServiceName()+") RequestID ("+getServiceDiscoveryRequest().getRequestID()+")");
				
			ObjectMapper mapper = new ObjectMapper();
			String requestServiceDiscovery = mapper.writeValueAsString(getServiceDiscoveryRequest());
			
			URL urlServiceDiscovery = new URL(peerURL);
			HttpsURLConnection connServiceDiscovery = (HttpsURLConnection) urlServiceDiscovery.openConnection();
			connServiceDiscovery.setRequestMethod("POST");
			connServiceDiscovery.setDoOutput(true);
			connServiceDiscovery.setInstanceFollowRedirects(false);
			connServiceDiscovery.setRequestProperty("Content-Type", "application/json");
			connServiceDiscovery.setRequestProperty("charset", "utf-8");
			connServiceDiscovery.setRequestProperty("Content-Length",
					"" + requestServiceDiscovery.getBytes(StandardCharsets.UTF_8).length);
			connServiceDiscovery.setUseCaches(false);
			DataOutputStream wrNotification = new DataOutputStream(connServiceDiscovery.getOutputStream());
			wrNotification.write(requestServiceDiscovery.getBytes());
			connServiceDiscovery.connect();
			String responsePayload = RequestUtils.getRequestAsString(connServiceDiscovery.getInputStream());
			connServiceDiscovery.disconnect();
			
			ServiceDiscoveryResponse sdresp = mapper.readValue(responsePayload.getBytes(),ServiceDiscoveryResponse.class);
			
			logger.debug("ChainNotifier.run() Notification response: cell (" + sdresp.getCellName()+ ") = " + sdresp.isRunning());
			
			if (sdresp.isRunning()){
				ServiceManager.addDiscoveryResponse(sdresp);
			}
		} catch (MalformedURLException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoverer.run()", "Failed to notify cell.");
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoverer.run()", "Failed to notify cell.");
		}
	}

}
