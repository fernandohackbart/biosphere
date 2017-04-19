package org.biosphere.tissue.services;

import java.util.Enumeration;
import java.util.Hashtable;

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
			Hashtable<String, String> statusTable = new Hashtable<String, String>();
			statusTable = ServiceManager.getStatus();
			Enumeration<String> serviceList = statusTable.keys();
			while (serviceList.hasMoreElements()) {
				String serviceName = serviceList.nextElement();
				logger.info("CellMonitor.run() Service: " + serviceName + " Status: " + statusTable.get(serviceName));
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
