package org.biosphere.tissue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.Service;
import org.biosphere.tissue.DNA.ServiceParameter;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.handlers.AbstractDefaultHandler;
import org.biosphere.tissue.handlers.ServiceNotFoundHandler;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.ServiceDiscoveryRequest;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
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
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceManager {
	public ServiceManager() {
		super();
	}

	private static Hashtable<String, ServiceInstance> cellServiceInstances = new Hashtable<String, ServiceInstance>();
	private static Hashtable<String, ArrayList<ServiceDiscoveryResponse>> cellServiceDiscoveries = new Hashtable<String, ArrayList<ServiceDiscoveryResponse>>();

	public static synchronized boolean isRunning(String serviceName) {
		// TODO check if the service is defined
		// TODO check if there is a instance
		// TODO check if the instance is running

		boolean isRunning = false;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case TissueManager.ThreadServiceClass:
				isRunning = cellServiceInstances.get(serviceName).getThreadService().isAlive();
				break;
			case TissueManager.ServletServiceClass:
				isRunning = cellServiceInstances.containsKey(serviceName);
				break;
			}
		}
		return isRunning;
	}

	public static synchronized boolean isInstantiated(String serviceName) {
		// TODO check if the service definition exists
		boolean isInstantiated = false;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case TissueManager.ThreadServiceClass:
				isInstantiated = cellServiceInstances.containsKey(serviceName);
				break;
			case TissueManager.ServletServiceClass:
				isInstantiated = cellServiceInstances.containsKey(serviceName);
				break;
			}
		}
		return isInstantiated;
	}

	public static boolean isEnabled(Cell cell, String serviceName) {
		return cell.getDna().getService(serviceName).isEnabled();
	}

	@SuppressWarnings("unchecked")
	public static String isContextDefined(Cell cell, String contextPath) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		String serviceName = null;
		if (contextPath != null) {
			for (Service service : cell.getDna().getServices()) {
				logger.trace("ServiceManager.isContextDefined() Checking service " + service.getName());
				if (service.getType().equals(TissueManager.ServletServiceClass)) {
					for (ServiceParameter sp : service.getParameters()) {
						logger.trace("ServiceManager.isContextDefined()   Checking service (" + service.getName()
								+ ") parameter (" + sp.getName() + ")");
						if (sp.getName().equals("Handlers")) {
							if (sp.getObjectValue() instanceof ArrayList<?>) {
								for (ServletHandlerDefinition shd : (ArrayList<ServletHandlerDefinition>) sp
										.getObjectValue()) {

									for (String shdc : shd.getContexts()) {
										if (shdc.equals(contextPath)) {
											logger.debug("ServiceManager.isContextDefined()     contextPath "
													+ contextPath + " defined in service " + service.getName() + "("
													+ service.getType() + ")");
											serviceName = service.getName();
											break;
										}
									}
								}
							} else {
								logger.error("ServiceManager.isContextDefined()   Parameter Handlers for service  "
										+ service.getName() + " is not instance of ArrayList!");
							}
						} else {
							logger.trace("ServiceManager.isContextDefined() Service (" + service.getName()
									+ ") parameter (" + sp.getName() + ") is not = \"Handlers\"");
						}
					}
				} else {
					logger.trace("ServiceManager.isContextDefined() Service (" + service.getName() + ") type ("
							+ service.getType() + ") is not of type (" + TissueManager.ServletServiceClass + ")");
				}
			}
		} else {
			logger.warn("ServiceManager.isContextDefined() provided contextPath is nulĺ!");
		}
		if (serviceName == null) {
			logger.warn(
					"ServiceManager.isContextDefined() contextPath " + contextPath + " not defined for any service!");
		}
		logger.trace("ServiceManager.isContextDefined() Context (" + contextPath + ") returning service  ("
				+ serviceName + ")!");
		return serviceName;
	}

	public static ServiceDiscoveryResponse discoverService(String serviceName, Cell cell, Service localService) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		ServiceDiscoveryRequest sdr = new ServiceDiscoveryRequest();
		sdr.setServiceName(serviceName);
		createDiscoveryResponse(sdr.getRequestID());
		ServiceDiscoveryResponse sdresp = null;
		try {

			// TODO if the service is running in another server in the same Cell
			// there is no need to contact remote cells, just get the port for
			// the other service for redirect
			if (!localService.getName().equals(sdr.getServiceName())) {
				logger.debug("ServiceManager.discoverService() serviceName (" + sdr.getServiceName()
						+ ") is not the service that received the request (" + localService.getName()
						+ "), checking to serve locally the request!");

				// TODO check if the service is enabled and running locally
				if (!ServiceManager.isRunning(sdr.getServiceName())) {
					ServiceManager.start(serviceName, cell);
				}
				sdresp = new ServiceDiscoveryResponse();
				sdresp.setRequestID(sdr.getRequestID());
				sdresp.setCellName(cell.getCellName());
				sdresp.setCellNetworkName(cell.getCellNetworkName());
				sdresp.setRunning(true);
				sdresp.setCellServicePort(
						(int) cell.getDna().getService(sdr.getServiceName()).getParameterValue("ServiceServletPort"));
			} else {
				logger.debug(
						"ServiceManager.discoverService() requesting discovery requestID (" + sdr.getRequestID() + ")");
				requestDiscovery(sdr, cell);
				logger.debug("ServiceManager.discoverService() waiting for discovery responses on requestID ("
						+ sdr.getRequestID() + ") service (" + sdr.getServiceName() + ")");
				boolean keepWaiting = true;
				long timeout = System.currentTimeMillis() + TissueManager.serviceDiscoveryTimeout;
				logger.trace("ServiceManager.discoverService() Will wait until " + new Date(timeout)
						+ " for discovery responses on requestID (" + sdr.getRequestID() + ") service ("
						+ sdr.getServiceName() + ")");
				while (keepWaiting) {
					if (!cellServiceDiscoveries.isEmpty()) {
						ArrayList<ServiceDiscoveryResponse> responses = cellServiceDiscoveries.get(sdr.getRequestID());
						sdresp = responses.get(0);
						logger.debug("ServiceManager.discoverService() Best response received for requestID ("
								+ sdr.getRequestID() + ") service (" + sdr.getServiceName() + ") is from cell ("
								+ sdresp.getCellName() + ") service listener (" + sdresp.getCellNetworkName() + ":"
								+ sdresp.getCellServicePort() + ")!");
						removeDiscoveryResponse(sdr.getRequestID());
						keepWaiting = false;
					}
					if (System.currentTimeMillis() > timeout) {
						logger.warn(
								"ServiceManager.discoverService() Timeout waiting for discovery responses on requestID ("
										+ sdr.getRequestID() + ") service (" + sdr.getServiceName() + ")");
						keepWaiting = false;
					}
					if (keepWaiting) {
						logger.trace("Chain.appendBlock(Block) Waiting for discovery responses on requestID ("
								+ sdr.getRequestID() + ") service (" + sdr.getServiceName() + ") for more "
								+ TissueManager.serviceDiscoveryInterval + " seconds");
						Thread.sleep(TissueManager.serviceDiscoveryInterval);
					}
				}
			}
		} catch (InterruptedException e) {
			ChainExceptionHandler.handleGenericException(e, "ServiceManager.discoverService()",
					"Failed to ddiscover service " + sdr.getServiceName() + " (Exception).");
		} catch (IOException e) {
			ChainExceptionHandler.handleGenericException(e, "ServiceManager.discoverService()",
					"Failed to ddiscover service " + sdr.getServiceName() + " (Exception).");
		}
		return sdresp;
	}

	private synchronized static void createDiscoveryResponse(String requestID) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.createDiscoveryResponse() Creating cellServiceDiscoveries entry for requestID ("
				+ requestID + ") !");
		cellServiceDiscoveries.put(requestID, new ArrayList<ServiceDiscoveryResponse>());
	}

	private synchronized static void removeDiscoveryResponse(String requestID) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.removeDiscoveryResponse() Removing cellServiceDiscoveries entry for requestID ("
				+ requestID + ") !");
		cellServiceDiscoveries.remove(requestID);
	}

	public synchronized static void addDiscoveryResponse(ServiceDiscoveryResponse sdresp) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		if (cellServiceDiscoveries.containsKey(sdresp.getRequestID())) {
			if (sdresp.isRunning()) {
				cellServiceDiscoveries.get(sdresp.getRequestID()).add(sdresp);
			}
		} else {
			logger.warn("ServiceManager.addDiscoveryResponse() cellServiceDiscoveries doest not contain requestID ("
					+ sdresp.getRequestID() + ") !");
		}
	}

	private static void requestDiscovery(ServiceDiscoveryRequest sdr, Cell cell) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		List<CellInterface> cellIFs = cell.getDna().getTissueCellsInterfaces();
		Iterator<CellInterface> cellIFIterator = cellIFs.iterator();
		while (cellIFIterator.hasNext()) {
			CellInterface cellInterface = cellIFIterator.next();
			if (!cellInterface.getCellName().equals(cell.getCellName())) {
				logger.debug("ServiceManager.requestDiscovery() Requesint service (" + sdr.getServiceName()
						+ ") discovery to cell (" + cellInterface.getCellName() + ") at "
						+ cellInterface.getCellNetworkName() + ":" + cellInterface.getPort());
				ServiceDiscoverer sd = new ServiceDiscoverer(cellInterface, sdr);
				Thread thread = new Thread(sd);
				thread.setName("ServiceDiscoverer-Service(" + sdr.getServiceName() + ")-Cell("
						+ cellInterface.getCellName() + ")");
				thread.start();
			}
		}
	}

	public static Hashtable<String, String> getStatus() {
		Hashtable<String, String> statusTable = new Hashtable<String, String>();
		Enumeration<String> serviceList = cellServiceInstances.keys();
		while (serviceList.hasMoreElements()) {
			String serviceName = (String) serviceList.nextElement();
			String serviceType = cellServiceInstances.get(serviceName).getType();
			switch (serviceType) {
			case TissueManager.ThreadServiceClass:
				statusTable.put(serviceName,
						"THREAD name:" + cellServiceInstances.get(serviceName).getThreadService().toString() + " state:"
								+ cellServiceInstances.get(serviceName).getThreadService().getState().toString()
								+ " daemon:" + cellServiceInstances.get(serviceName).getThreadService().isDaemon());
				break;
			case TissueManager.ServletServiceClass:
				statusTable.put(serviceName, cellServiceInstances.get(serviceName).getJettyServer().getState());
				break;
			}
		}
		return statusTable;
	}

	public static StringBuffer getServletStatus(String serviceName) {
		StringBuffer statusTable = new StringBuffer();
		if (cellServiceInstances.get(serviceName).getType().equals(TissueManager.ServletServiceClass)) {
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

	private static void instantiate(Service service, Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.load() Loading " + service.getType() + " service " + service.getName());
		switch (service.getType()) {
		case TissueManager.ThreadServiceClass:
			instantiateTHREAD(service, cell);
			break;
		case TissueManager.ServletServiceClass:
			instantiateServlet(service, cell);
			break;
		}
	}

	private static void instantiateTHREAD(Service service, Cell cell) {
		try {
			Class<?> toStartServiceClass = loadClass(service.getClassName());
			THREADService toStartService = (THREADService) toStartServiceClass.newInstance();
			toStartService.setCell(cell);
			toStartService.setName(service.getName());
			toStartService.setParameters(service.getParameters());
			toStartService.setDaemon(service.isDaemon());
			// cellTHREADServiceInstances.put(service.getServiceName(),toStartService);
			ServiceInstance serviceInstance = new ServiceInstance(service);
			serviceInstance.setThreadService(toStartService);
			cellServiceInstances.put(service.getName(), serviceInstance);
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + service.getClassName());
		} catch (IllegalAccessException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + service.getClassName());
		} catch (InstantiationException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.loadService()",
					"Could not instantiate ServiceInterface " + service.getClassName());
		}
	}

	@SuppressWarnings("unchecked")
	private static void instantiateServlet(Service service, Cell cell) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = 0;
		try {
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(TissueManager.jettyMaxThreadPoolSize);
			threadPool.setName(service.getName());
			Server server = new Server(threadPool);

			ContextHandlerCollection contexts = new ContextHandlerCollection();

			// Service handlers
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			ArrayList<ServletHandlerDefinition> cellTissueListenerHandlers = (ArrayList<ServletHandlerDefinition>) service
					.getParameterValue("Handlers");
			for (ServletHandlerDefinition cellTissueListenerHandler : cellTissueListenerHandlers) {
				Class<?> handlerClass = loadClass(cellTissueListenerHandler.getClassName());
				CellServletHandlerInterface toStartHandler = (CellServletHandlerInterface) handlerClass.newInstance();
				toStartHandler.setCell(cell);
				toStartHandler.setContentType(cellTissueListenerHandler.getContentType());
				toStartHandler.setContentEncoding(cellTissueListenerHandler.getContentEncoding());
				ArrayList<String> handlerContexts = cellTissueListenerHandler.getContexts();
				for (String contextURI : handlerContexts) {
					context.addServlet(new ServletHolder((Servlet) toStartHandler), contextURI);
					logger.debug("ServiceManager.loadServlet()" + service.getName() + " handler "
							+ cellTissueListenerHandler.getClassName() + " added context: ("
							+ cellTissueListenerHandler.getContentType() + ") " + contextURI);
				}
			}
			// Adding the error handler for the servlet context
			ServiceNotFoundHandler errorMapper = new ServiceNotFoundHandler();
			errorMapper.setCell(cell);
			errorMapper.setService(service);
			errorMapper.setContentType(TissueManager.defaultContentType);
			errorMapper.setContentEncoding(TissueManager.defaultContentEncoding);
			logger.debug("ServiceManager.loadServlet()" + service.getName()
					+ " added error handler org.biosphere.tissue.handlers.ServiceNotFoundHandler");
			context.setErrorHandler(errorMapper);

			// Adding the context to the server set of contexts
			contexts.addHandler(context);

			// Default handler for the server
			logger.debug("ServiceManager.loadServlet()" + service.getName() + " added default handler "
					+ service.getParameterValue("DefaultHandler"));
			Class<?> handlerClass = loadClass((String) service.getParameterValue("DefaultHandler"));
			AbstractDefaultHandler adh = (AbstractDefaultHandler) handlerClass.newInstance();
			adh.setCell(cell);
			adh.setService(service);
			adh.setContentEncoding(TissueManager.defaultContentEncoding);
			adh.setContentType(TissueManager.defaultContentType);
			contexts.addHandler((DefaultHandler) adh);

			server.setHandler(contexts);

			HTTPPort = (Integer) service.getParameterValue("DefaultHTTPPort");

			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStore(cell.getCellKeystore());
			sslContextFactory.setCertAlias(cell.getCellName());
			sslContextFactory.setKeyStorePassword(cell.getCellKeystorePWD());
			sslContextFactory.setKeyManagerPassword(cell.getCellKeystorePWD());
			sslContextFactory.setTrustStore(cell.getCellKeystore());
			sslContextFactory.setTrustStorePassword(cell.getCellKeystorePWD());
			sslContextFactory.setProtocol("TLSv1.2");
			// sslContextFactory.setIncludeCipherSuites("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
			HttpConfiguration hsc = new HttpConfiguration();
			hsc.setSecureScheme("https");
			hsc.setSecurePort(HTTPPort);
			hsc.setOutputBufferSize(TissueManager.jettyOutputBufferSize);
			hsc.setRequestHeaderSize(TissueManager.jettyRequestHeaderSize);
			hsc.setResponseHeaderSize(TissueManager.jettyResponseHeaderSize);
			hsc.setSendServerVersion(TissueManager.jettySendServerVersion);
			hsc.setSendDateHeader(TissueManager.jettySendDateHeader);
			hsc.addCustomizer(new SecureRequestCustomizer());
			ServerConnector httpsConnector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(hsc));
			httpsConnector.setPort(HTTPPort);
			server.addConnector(httpsConnector);
			ServiceInstance serviceInstance = new ServiceInstance(service);
			serviceInstance.setJettyServer(server);
			serviceInstance.setJettyServerConnector(httpsConnector);
			serviceInstance.setJettyContexts(contexts);
			cellServiceInstances.put(service.getName(), serviceInstance);
		} catch (Exception e) {
			TissueExceptionHandler.handleGenericException(e, "CellManager.loadServlet()", "Exception:");
		}
	}

	public static synchronized void start(String serviceName, Cell cell) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		Service service = cell.getDna().getService(serviceName);
		try {
			logger.info("ServiceManager.start() Starting " + service.getType() + " service " + service.getName());
			startService(service, cell);
		} catch (CellException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.start(",
					"Service " + serviceName + " exception:" + e.getMessage());
		}
	}

	public static synchronized int startService(Service service, Cell cell) throws CellException {
		int HTTPPort = 0;
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		if (service instanceof Service) {
			if (service.isEnabled()) {
				if (isRunning(service.getName())) {
					logger.warn("ServiceManager.startService()" + service.getType() + " service " + service.getName()
							+ " already running");
				} else {
					try {
						if (!isInstantiated(service.getName())) {
							instantiate(service, cell);
						}
						HTTPPort = startServiceInstance(service.getName(), cell);
						if (service.getType().equals(TissueManager.ServletServiceClass)) {
							if (!service.getName()
									.equals(CellManager.getCellTissueServletListenerDefinition().getName())) {
								cell.getDna().getService(service.getName()).addParameter("ServiceServletPort",
										HTTPPort);
							} else {
								logger.warn("ServiceManager.startService()" + service.getType() + " service " + service.getName()
								+ " not not creating ServiceServletPort parameter, service discovery may fail to find the redirect port!");
							}

						}
					} catch (Exception e) {
						TissueExceptionHandler.handleGenericException(e, "ServiceManager.startService()",
								"Could not start " + service.getType() + " service " + service.getName());
					}
				}
			} else {
				logger.warn("ServiceManager.startService()" + service.getType() + " service " + service.getName()
						+ " not enabled, skipping startup");
			}
		} else {
			TissueExceptionHandler.handleUnrecoverableGenericException(new Exception(), "ServiceManager.startService",
					"Service definition is not valid!");
		}
		return HTTPPort;
	}

	public static synchronized int startServiceInstance(String serviceName, Cell cell) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = 0;
		if (cellServiceInstances.containsKey(serviceName)) {
			String serviceType = cellServiceInstances.get(serviceName).getType();
			logger.debug(
					"ServiceManager.startServiceInstance() Starting " + serviceType + " service " + serviceName + "!");
			switch (serviceType) {
			case TissueManager.ThreadServiceClass:
				cellServiceInstances.get(serviceName).getThreadService().start();
				break;
			case TissueManager.ServletServiceClass:
				HTTPPort = startServlet(cellServiceInstances.get(serviceName), cell);
				break;
			}
		} else {
			logger.debug("ServiceManager.startServiceInstance() Service " + serviceName + " not loaded!");
		}
		return HTTPPort;

	}

	private static int startServlet(ServiceInstance serviceInstance, Cell cell) {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		int HTTPPort = serviceInstance.getJettyServerConnector().getPort();
		boolean listening = false;
		try {
			while (!listening) {
				try {
					serviceInstance.getJettyServerConnector().setPort(HTTPPort);
					serviceInstance.getJettyServer().start();
					// serviceInstance.getJettyServer().join();
					logger.info(serviceInstance.getName() + "listening at " + HTTPPort + "!");
					listening = true;
				} catch (java.net.BindException e) {
					logger.debug("ServiceManager.startServlet() Port: " + HTTPPort
							+ " is used incrementing by 1 and retrying!");
					HTTPPort++;
					serviceInstance.getJettyServer().stop();
					if (HTTPPort > 65535) {
						throw new CellException("ServiceManager.startServlet()",
								"Maximum port number (65535) reached, aborting startup.");
					}
				}
			}
		} catch (Exception e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceManager.startServlet",
					"Exception in service " + serviceInstance.getName());
		}
		return HTTPPort;
	}

	public static synchronized void stop(String serviceType, String serviceName) throws CellException {
		Logger logger = LoggerFactory.getLogger(ServiceManager.class);
		logger.debug("ServiceManager.stop() Stopping " + serviceType + " service " + serviceName);

		switch (serviceType) {
		case TissueManager.ThreadServiceClass:
			stopTHREAD(serviceName);
			break;
		case TissueManager.ServletServiceClass:
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
				throw new CellException("ServiceManager.stopServlet()",
						"Service " + serviceName + " Exception: " + e.getLocalizedMessage());
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
			case TissueManager.ServletServiceClass:
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
			if (isInstantiated(serviceName)) {
				ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				Class<?> handlerClass = loadClass(shd.getClassName());
				CellServletHandlerInterface toStartHandler = (CellServletHandlerInterface) handlerClass.newInstance();
				toStartHandler.setCell(cell);
				toStartHandler.setContentType(shd.getContentType());
				toStartHandler.setContentEncoding(shd.getContentEncoding());
				for (String contextURI : shd.getContexts()) {
					context.addServlet(new ServletHolder((Servlet) toStartHandler), contextURI);
					logger.debug("ServiceManager.addServletContext() Handler " + shd.getClassName()
							+ " added context: (" + shd.getContentType() + ") " + contextURI);
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
