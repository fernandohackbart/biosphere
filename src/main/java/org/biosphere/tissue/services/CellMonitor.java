package org.biosphere.tissue.services;

import java.util.ArrayList;
import org.biosphere.tissue.protocol.ServiceStatusItem;

public class CellMonitor extends THREADService {
	public CellMonitor() {
		super();
	}

	@Override
	public void run() {
		boolean keepRunnig = true;
		while (keepRunnig) {
			logger.info("CellMonitor.run() ##########################################################");
			logger.info("CellMonitor.run() Monitor check ");
			ArrayList<ServiceStatusItem> statusList = (ArrayList<ServiceStatusItem>) ServiceManager.getStatus();
			for (ServiceStatusItem ssi : statusList) {
				logger.info("CellMonitor.run() Service: " + ssi.getServiceName() + " Status: " + ssi.getServiceStatus());
			}
			logger.info("CellMonitor.run() ##########################################################");
			try {
				java.lang.Thread.sleep((Long) getParameter("Interval"));
			} catch (InterruptedException e) {
				// ExceptionHandler.handleGenericException(e,"CellMonitor.run()","Monitor
				// signaled to stop, exiting!:");
				logger.info("CellMonitor.run() Monitor signaled to stop, exiting!");
				ServiceManager.stopServletServices();
				keepRunnig = false;
			}
		}
	}

}
