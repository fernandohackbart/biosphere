package org.biosphere.tissue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.handlers.CellServletHandlerInterface;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceManager {
	public ServiceManager() {
		super();
	}

	private static Hashtable<String, ServiceInstance> cellServiceInstances = new Hashtable<String, ServiceInstance>();

	public static synchronized boolean isRunning(String serviceName) {
		boolean isRunning = false;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case "THREAD":
				isRunning = cellServiceInstances.get(serviceName).getThreadService().isAlive();
				break;
			case "SERVLET":
				isRunning = cellServiceInstances.containsKey(serviceName);
				break;
			}
		}
		return isRunning;
	}

	public static synchronized boolean isLoaded(String serviceName) {
		boolean isLoaded = false;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case "THREAD":
				isLoaded = cellServiceInstances.containsKey(serviceName);
				break;
			case "SERVLET":
				isLoaded = cellServiceInstances.containsKey(serviceName);
				break;
			}
		}
		return isLoaded;
	}

	public static Hashtable<String, String> getStatus() {
		Hashtable<String, String> statusTable = new Hashtable<String, String>();
		Enumeration<String> serviceList = cellServiceInstances.keys();
		while (serviceList.hasMoreElements()) {
			String serviceName = (String) serviceList.nextElement();
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case "THREAD":
				statusTable.put(serviceName,
						"THREAD name:" + cellServiceInstances.get(serviceName).getThreadService().toString() + " state:"
								+ cellServiceInstances.get(serviceName).getThreadService().getState().toString()
								+ " daemon:" + cellServiceInstances.get(serviceName).getThreadService().isDaemon());
				break;
			case "SERVLET":
				statusTable.put(serviceName, cellServiceInstances.get(serviceName).getJettyServer().getState());
				break;
			}
		}
		return statusTable;
	}

	public static StringBuffer getServletStatus(String serviceName) {
		StringBuffer statusTable = new StringBuffer();
     	if(cellServiceInstances.get(serviceName).getType().equals("SERVLET"))
     	{
			try {
				cellServiceInstances.get(serviceName).getJettyServer().dump(statusTable);
			} catch (IOException e) {
				TissueExceptionHandler.handleGenericException(e, "ServiceManager.getServletStatus()",
						"Could not get status for servlet  " + serviceName);
			}
     		
     	}
		return statusTable;
	}
	
	private static Class<?> loadClass(String className) throws CellException {
		Class<?> serviceClass = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			serviceClass = cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			TissueExceptionHandler.handleUnrecoverableGenericException(e, "ServiceManager.loadClass()",
					"Class " + className + " not found!");
		}
		return serviceClass;
	}

	private static void load(ServiceDefinition serviceDefinition, Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.load() Loading " + serviceDefinition.getType() + " service "
				+ serviceDefinition.getName());
		switch (serviceDefinition.getType()) {
		case "THREAD":
			loadTHREAD(serviceDefinition, cell);
			break;
		case "SERVLET":
			loadServlet(serviceDefinition, cell);
			break;
		}
	}

	private static void loadTHREAD(ServiceDefinition sd, Cell cell) {
		try {
			Class<?> toStartServiceClass = loadClass(sd.getClassName());
			THREADService toStartService = (THREADService) toStartServiceClass.newInstance();
			toStartService.setCell(cell);
			toStartService.setName(sd.getName());
			toStartService.setParameters(sd.getParameters());
			toStartService.setDaemon(sd.isDaemon());
			// cellTHREADServiceInstances.put(serviceDefinition.getServiceDefinitionName(),toStartService);
			ServiceInstance serviceInstance = new ServiceInstance(sd);
			serviceInstance.setThreadService(toStartService);
			cellServiceInstances.put(sd.getName(), serviceInstance);
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + sd.getClassName());
		} catch (IllegalAccessException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + sd.getClassName());
		} catch (InstantiationException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + sd.getClassName());
		}
	}

	private static void loadServlet(ServiceDefinition sd, Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = 0;
        try {
			Server server = new Server();
     		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			ArrayList<ServletHandlerDefinition> cellTissueListenerHandlers = (ArrayList<ServletHandlerDefinition>) sd.getParameters().get("Handlers");			
			for (ServletHandlerDefinition cellTissueListenerHandler : cellTissueListenerHandlers)
			{
				Class<?> handlerClass = loadClass(cellTissueListenerHandler.getClassName());
				CellServletHandlerInterface toStartHandler = (CellServletHandlerInterface)handlerClass.newInstance();
				toStartHandler.setCell(cell);
				toStartHandler.setContentType(cellTissueListenerHandler.getContentType());
				toStartHandler.setContentEncoding(cellTissueListenerHandler.getContentEncoding());
				ArrayList<String> handlerContexts = cellTissueListenerHandler.getContexts();
				for (String contextURI : handlerContexts) {
					context.addServlet(new ServletHolder((Servlet)toStartHandler), contextURI);
					logger.debug("ServiceManager.loadServlet()" +
							sd.getName() + " handler " + cellTissueListenerHandler.getClassName() + " added context: (" +cellTissueListenerHandler.getContentType()+") "+ contextURI);
				}				
			}
			ContextHandlerCollection contexts = new ContextHandlerCollection(); 
			contexts.addHandler(context);
			contexts.addHandler(new DefaultHandler());
			server.setHandler(contexts);
			
			HTTPPort = (Integer) sd.getParameters().get("DefaultHTTPPort");
			
	        SslContextFactory sslContextFactory = new SslContextFactory();
	        sslContextFactory.setKeyStore(cell.getCellKeystore());
	        sslContextFactory.setCertAlias(cell.getCellName());
	        sslContextFactory.setKeyStorePassword(cell.getCellKeystorePWD());
	        sslContextFactory.setKeyManagerPassword(cell.getCellKeystorePWD());
	        sslContextFactory.setTrustStore(cell.getCellKeystore());
	        sslContextFactory.setTrustStorePassword(cell.getCellKeystorePWD());
	        sslContextFactory.setProtocol("TLSv1.2");
	        //sslContextFactory.setIncludeCipherSuites("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
	        HttpConfiguration hsc = new HttpConfiguration();
	        hsc.setSecureScheme("https");
	        hsc.setSecurePort(HTTPPort);
	        hsc.setOutputBufferSize(32768);
	        hsc.setRequestHeaderSize(8192);
	        hsc.setResponseHeaderSize(8192);
	        hsc.setSendServerVersion(true);
	        hsc.setSendDateHeader(false);
	        hsc.addCustomizer(new SecureRequestCustomizer());
	        ServerConnector httpsConnector = new ServerConnector(server,
	                new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
	                new HttpConnectionFactory(hsc));
	        httpsConnector.setPort(HTTPPort);
	        server.addConnector(httpsConnector);
			ServiceInstance serviceInstance = new ServiceInstance(sd);
			serviceInstance.setJettyServer(server);
			serviceInstance.setJettyServerConnector(httpsConnector);
			serviceInstance.setJettyContexts(contexts);
			cellServiceInstances.put(sd.getName(), serviceInstance);
		} catch (Exception e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServlet()","Exception:");
		}
	}
	
	public static synchronized void start(String serviceName, Cell cell) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		//TODO replace by the JSON DNA
		//ServiceDefinition sd = cell.getCellXMLDNA().getServiceDefinition(serviceName);
		ServiceDefinition sd = cell.getDna().getService(serviceName);
		try {
			logger.info("ServiceManager.start() Starting " + sd.getType() + " service " + sd.getName());
			startServiceDefinition(sd, cell);
			if (sd.getType().equals("SERVLET")) {
				// TODO add HTTPPort to the DNA service parameters
			}
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.start(",
					"Service " + serviceName + " exception:" + e.getMessage());
		}
	}

	public static synchronized int startServiceDefinition(ServiceDefinition sd, Cell cell) throws CellException {
		int HTTPPort = 0;
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		if (sd instanceof ServiceDefinition) {
			if (isRunning(sd.getName())) {
				logger.info("ServiceManager.startServiceDefinition()"+sd.getType() + " service "
						+ sd.getName() + " already running");
			} else {
				try {
					if (!isLoaded(sd.getName())) {
						load(sd, cell);
					}
					HTTPPort = startServiceInstance(sd.getName());
				} catch (Exception e) {
					TissueExceptionHandler.handleGenericException(e, "ServiceManager.startServiceDefinition()",
							"Could not start " + sd.getType() + " service "
									+ sd.getName());
				}
			}
		} else {
			TissueExceptionHandler.handleUnrecoverableGenericException(new Exception(),
					"ServiceManager.startServiceDefinition", "Service definition is not valid!");
		}
		return HTTPPort;
	}

	public static synchronized int startServiceInstance(String serviceName) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = 0;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			logger.debug("ServiceManager.startServiceInstance() Starting " + serviceType + " service " + serviceName + "!");
			switch (serviceType) {
			case "THREAD":
				cellServiceInstances.get(serviceName).getThreadService().start();
				break;
			case "SERVLET":
				HTTPPort = startServlet(cellServiceInstances.get(serviceName));
				break;
			}
		} else {
			logger.debug("ServiceManager.startServiceInstance() Service " + serviceName + " not loaded!");
		}
        return HTTPPort;
		
	}

	private static int startServlet(ServiceInstance serviceInstance)
	{
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = serviceInstance.getJettyServerConnector().getPort();
		boolean listening = false;
		try
		{
			while (!listening) {
				try { 
					serviceInstance.getJettyServerConnector().setPort(HTTPPort);
					serviceInstance.getJettyServer().start();
			        //serviceInstance.getJettyServer().join();
					logger.info(serviceInstance.getName()+ "listening at " + HTTPPort + "!");
					listening = true;
				} catch (java.net.BindException e) {
					logger.debug("ServiceManager.startServlet() Port: " +  HTTPPort + " is used incrementing by 1 and retrying!");
					HTTPPort++;
					serviceInstance.getJettyServer().stop();
					if (HTTPPort>65535)
					{
						throw new CellException("ServiceManager.startServlet()", "Maximum port number (65535) reached, aborting startup.");
					}
				}
			}			
		}
		catch (Exception e)
		{
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.startServlet", "Excpetion in service "+serviceInstance.getName());
		}
		return HTTPPort;
	}
	
	public static synchronized void stop(String serviceType, String serviceName) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.stop() Stopping " + serviceType + " service " + serviceName);

		switch (serviceType) {
		case "THREAD":
			stopTHREAD(serviceName);
			break;
		case "SERVLET":
			stopServlet(serviceName);
			break;
		}
	}

	private static void stopTHREAD(String serviceName) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		if (cellServiceInstances.containsKey(serviceName)) {
			if (isRunning(serviceName)) {
				logger.debug("ServiceManager.stop() Interrupting " + serviceName);
				cellServiceInstances.get(serviceName).getThreadService().interrupt();
				cellServiceInstances.remove(serviceName);
			} else {
				throw new CellException("ServiceManager.stopTHREAD()", "Service " + serviceName + " not running.");
			}
		} else {
			throw new CellException("ServiceManager.stopTHREAD()", "Service " + serviceName + " not found.");
		}
	}

	private static void stopServlet(String serviceName) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		if (isRunning(serviceName)) {
			logger.debug("ServiceManager.stopHTTPService() Stopping ServletService " + serviceName);
			try {
				cellServiceInstances.get(serviceName).getJettyServer().stop();
				cellServiceInstances.get(serviceName).getJettyServer().destroy();
			} catch (Exception e) {
				throw new CellException("ServiceManager.stopServlet()", "Service " + serviceName + " Exception: "+e.getLocalizedMessage());
			}
			cellServiceInstances.remove(serviceName);
		} else {
			throw new CellException("ServiceManager.stopServlet()", "Service " + serviceName + " not found.");
		}
	}

	public static synchronized void stopServletServices() {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.stopHTTPServices() Stopping all ServletServices!");
		Enumeration<String> servletServiceList = cellServiceInstances.keys();
		while (servletServiceList.hasMoreElements()) {
			String serviceName = (String) servletServiceList.nextElement();
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case "SERVLET":
				try {
					stopServlet(serviceName);
				} catch (CellException e) {
					TissueExceptionHandler.handleGenericException(e, "ServiceManager.stopServletServices()",
							"Could not stop ServletService " + serviceName);
				}
				break;
			}

		}
	}

	public static synchronized void addServletContext(String serviceName, ServletHandlerDefinition shd, Cell cell) {
		try {
			Logger logger = LoggerFactory.getLogger(ServiceManager.class);
			if (isLoaded(serviceName)) {
				ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				Class<?> handlerClass = loadClass(shd.getClassName());
				CellServletHandlerInterface toStartHandler = (CellServletHandlerInterface)handlerClass.newInstance();
				toStartHandler.setCell(cell);
				toStartHandler.setContentType(shd.getContentType());
				toStartHandler.setContentEncoding(shd.getContentEncoding());
				for (String contextURI : shd.getContexts()) {
					context.addServlet(new ServletHolder((Servlet)toStartHandler), contextURI);
					logger.debug("ServiceManager.addServletContext() Handler " + shd.getClassName() + " added context: (" + shd.getContentType() +") " + contextURI);
				}
				cellServiceInstances.get(serviceName).getJettyContexts().addHandler(context);
			}
		} catch (IllegalAccessException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.addServletContext()",
					"IllegalAccessException:");
		} catch (InstantiationException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.addServletContext()",
					"InstantiationException:");
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.addServletContext()", "CellException:");
		}
	}
}
