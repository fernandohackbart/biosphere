package org.biosphere.tissue.services;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.handlers.CellHTTPHandlerInterface;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.HTTPSSLConfigurator;
import org.biosphere.tissue.utils.Logger;

public final class ServiceManager
{
  public ServiceManager()
  {
    super();
  }
 
  //private static Hashtable<String,THREADService> cellTHREADServiceInstances = new Hashtable<String,THREADService>();
  //private static Hashtable<String,HttpServer> cellHTTPServiceInstances = new Hashtable<String,HttpServer>();
  private static Hashtable<String,ServiceInstance> cellServiceInstances = new Hashtable<String,ServiceInstance>();
  
  public static synchronized boolean isRunning(String serviceName)
  {
    boolean isRunning=false;
    if (cellServiceInstances.containsKey(serviceName))
    {
      String serviceType=cellServiceInstances.get(serviceName).getServiceDefinitionType();
      switch (serviceType) 
      {
        case "THREAD":
          isRunning = cellServiceInstances.get(serviceName).getThreadService().isAlive();  
          break;
        case "HTTP": 
          isRunning = cellServiceInstances.containsKey(serviceName);
          break;
      }
    }
    return isRunning;
  }
  
  public static synchronized boolean isLoaded(String serviceName)
  {
    boolean isLoaded=false;
    if (cellServiceInstances.containsKey(serviceName))
    {
      String serviceType=cellServiceInstances.get(serviceName).getServiceDefinitionType();
      switch (serviceType) 
      {
        case "THREAD": isLoaded = cellServiceInstances.containsKey(serviceName);
          break;
        case "HTTP": isLoaded = cellServiceInstances.containsKey(serviceName);
          break;
      }
    }
    return isLoaded;
  }
  
  public static Hashtable<String,String> getStatus()
  {
    Hashtable<String,String> statusTable = new Hashtable<String,String>();
    Enumeration serviceList = cellServiceInstances.keys();
    while(serviceList.hasMoreElements())
    {
      String serviceName = (String)serviceList.nextElement();
      String serviceType=cellServiceInstances.get(serviceName).getServiceDefinitionType();
      switch (serviceType) 
      {
        case "THREAD": statusTable.put(serviceName,"THREAD name:"+cellServiceInstances.get(serviceName).getThreadService().toString()+" state:"+cellServiceInstances.get(serviceName).getThreadService().getState().toString()+" daemon:"+cellServiceInstances.get(serviceName).getThreadService().isDaemon());
          break;
        case "HTTP": statusTable.put(serviceName,"HTTP services have no status so far!");
          break;
      }
    }
    return statusTable;
  }
  
  private static Class loadClass(String className) throws CellException
  {
    Class serviceClass=null;
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try
    {
      serviceClass=cl.loadClass(className);
    }
    catch (ClassNotFoundException e)
    {
      TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadClass()","Class "+className+" not found!");
    }
    return serviceClass;
  }  
  
  private static int load(ServiceDefinition serviceDefinition,Cell cell) throws CellException
  {
    int HTTPPort = 0;
    Logger logger = new Logger();
    logger.debug("ServiceManager.load()","Loading "+serviceDefinition.getServiceDefinitionType()+" service "+serviceDefinition.getServiceDefinitionName());
    switch (serviceDefinition.getServiceDefinitionType()) 
    {
      case "THREAD": loadTHREAD(serviceDefinition,cell);
        break;
      case "HTTP":  HTTPPort=loadHTTP(serviceDefinition,cell);
        break;
    }
    return HTTPPort;
  }  
  
  private static void loadTHREAD(ServiceDefinition sd,Cell cell)
  {
    try
    {
      Class toStartServiceClass = loadClass(sd.getServiceDefinitionClass());
      THREADService toStartService = (THREADService)toStartServiceClass.newInstance();
      toStartService.setCell(cell);
      toStartService.setName(sd.getServiceDefinitionName());
      toStartService.setParameters(sd.getServiceDefinitionParameters());
      toStartService.setDaemon(sd.isServiceDefinitionDaemon());
      
      //cellTHREADServiceInstances.put(serviceDefinition.getServiceDefinitionName(),toStartService);
      ServiceInstance serviceInstance = new ServiceInstance(sd);
      serviceInstance.setThreadService(toStartService);
      cellServiceInstances.put(sd.getServiceDefinitionName(),serviceInstance);
    }
    catch (CellException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.loadService()","Could not instantiate ServiceInterface "+sd.getServiceDefinitionClass());
    }
    catch (IllegalAccessException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.loadService()","Could not instantiate ServiceInterface "+sd.getServiceDefinitionClass());
    }
    catch(InstantiationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.loadService()","Could not instantiate ServiceInterface "+sd.getServiceDefinitionClass());
    }    
  }
  
  private static int loadHTTP(ServiceDefinition sd,Cell cell) throws CellException
  {
    Logger logger = new Logger();
    int HTTPPort = 0;
    try
    {
      HttpsServer server = null;
      boolean listening=false;
      HTTPPort = (Integer)sd.getServiceDefinitionParameters().get("DefaultHTTPPort");
      while (!listening)
      {
        try
        {
          server = HttpsServer.create(new InetSocketAddress(HTTPPort), 0);
          logger.info(sd.getServiceDefinitionName(),"listening at "+HTTPPort+"!");
          sd.addServiceDefinitionParameter("HTTPPort",HTTPPort);
          listening=true;
        }
        catch (java.net.BindException e)
        {
          HTTPPort++;
        }
      }   
      
      server.setExecutor(Executors.newCachedThreadPool());
      
      try
      {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(cell.getCellKeystore(),cell.getCellKeystorePWD().toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(cell.getCellKeystore());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        HttpsConfigurator httpSSLConfigurator = new HTTPSSLConfigurator(sslContext);
        server.setHttpsConfigurator(httpSSLConfigurator);          
      }
      
      catch (NoSuchAlgorithmException e)
      {
        TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTP()","Exception:");
      }
      catch (KeyStoreException | UnrecoverableKeyException e)
      {
        TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTP()","Exception:");
      }
      catch (KeyManagementException e)
      {
        TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTP()","KeyManagementException:");
      }
      
      @SuppressWarnings("unchecked")
      Hashtable<String,ArrayList> cellTissueListenerHandlers = (Hashtable<String, ArrayList>)sd.getServiceDefinitionParameters().get("Handlers");
      Enumeration handlersList = cellTissueListenerHandlers.keys();
      while(handlersList.hasMoreElements())
      {
        String handlerClassName = (String) handlersList.nextElement();
        Class handlerClass = loadClass(handlerClassName);
        CellHTTPHandlerInterface toStartHandler;
        toStartHandler = (CellHTTPHandlerInterface) handlerClass.newInstance();
        toStartHandler.setCell(cell);
        @SuppressWarnings("unchecked")
        ArrayList<String> handlerContexts = cellTissueListenerHandlers.get(handlerClassName);
        for (String context : handlerContexts) 
        {
          server.createContext(context,toStartHandler);
          logger.debug("ServiceManager.loadHTTPService()","Handler "+handlerClassName+" added context: "+context);
        }
      }
      
      //cellHTTPServiceInstances.put(sd.getServiceDefinitionName(),server);
      ServiceInstance serviceInstance = new ServiceInstance(sd);
      serviceInstance.setHttpServer(server);
      cellServiceInstances.put(sd.getServiceDefinitionName(),serviceInstance);
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTPService()","IOException:");
    }  
    catch (IllegalAccessException e)
    {
      TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTPService()","IllegalAccessException:");
    }
    catch (InstantiationException e)
    {
      TissueExceptionHandler.handleUnrecoverableGenericException(e,"ServiceManager.loadHTTPService()","InstantiationException:");
    }
    return HTTPPort;
  }
  
  public static synchronized void start(String serviceName,Cell cell)
  {
    Logger logger = new Logger();
    ServiceDefinition sd = cell.getCellDNA().getServiceDefinition(serviceName);
    try
    {
      int HTTPPort = 0;
      logger.info("ServiceManager.start()","Starting " + sd.getServiceDefinitionType()+" service "+sd.getServiceDefinitionName());
      HTTPPort = startServiceDefinition(sd,cell);
      if (sd.getServiceDefinitionType().equals("HTTP"))
      {
        //TODO  add HTTPPort to the DNA service parameters
      }
    }
    catch (CellException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.start(","Service "+serviceName+" exception:"+e.getMessage());
    }
  }
  
  public static synchronized int startServiceDefinition(ServiceDefinition sd,Cell cell) throws CellException
  {
    int HTTPPort = 0;
    Logger logger = new Logger();
    if (sd instanceof ServiceDefinition)
    {
      if (isRunning(sd.getServiceDefinitionName()))
      {
        logger.info("ServiceManager.startServiceDefinition()",sd.getServiceDefinitionType()+" service "+sd.getServiceDefinitionName()+" already running");
      }
      else
      {
        try
        {
          if (!isLoaded(sd.getServiceDefinitionName()))
          {
            HTTPPort = load(sd,cell);
          }
          startServiceInstance(sd.getServiceDefinitionName());
        }
        catch (Exception e)
        {
          TissueExceptionHandler.handleGenericException(e,"ServiceManager.startServiceDefinition()","Could not start "+sd.getServiceDefinitionType()+" service "+sd.getServiceDefinitionName());
        }
      }      
    }
    else
    {
      TissueExceptionHandler.handleUnrecoverableGenericException(new Exception(), "ServiceManager.startServiceDefinition","Service definition is not valid!" );
    }
    return HTTPPort;
  }
  
  public static synchronized void startServiceInstance(String serviceName)
  {      
    Logger logger = new Logger();
    if (cellServiceInstances.containsKey(serviceName))
    {
      String serviceType=cellServiceInstances.get(serviceName).getServiceDefinitionType();
      logger.debug("ServiceManager.startServiceInstance()","Starting "+serviceType+" service "+serviceName+"!");
      switch (serviceType) 
      {
        case "THREAD": 
          cellServiceInstances.get(serviceName).getThreadService().start();  
          break;
        case "HTTP": 
          cellServiceInstances.get(serviceName).getHttpServer().start();  
          break;
      }
    }
    else
    {
      logger.debug("ServiceManager.startServiceInstance()","Service "+serviceName+" not loaded!");
    }
    
  }
  
  public static synchronized void stop(String serviceType,String serviceName) throws CellException
  {      
    Logger logger = new Logger();
    logger.debug("ServiceManager.stop()","Stopping "+serviceType+" service "+serviceName);
    
    switch (serviceType) 
    {
      case "THREAD": stopTHREAD(serviceName);
        break;
      case "HTTP": stopHTTP(serviceName);
        break;
    }
  }
  
  private static void stopTHREAD(String serviceName) throws CellException
  {
    Logger logger = new Logger();
    if (cellServiceInstances.containsKey(serviceName))
    {
      if (isRunning(serviceName))
      {
        logger.debug("ServiceManager.stop()","Interrupting "+serviceName);
        cellServiceInstances.get(serviceName).getThreadService().interrupt();
        cellServiceInstances.remove(serviceName);
      }
      else
      {
        throw new CellException("ServiceManager.stop()","Service "+serviceName+" not running.");
      }
    }
    else
    {
      throw new CellException("ServiceManager.stop()","Service "+serviceName+" not found.");
    }
  }
  
  private static void stopHTTP(String serviceName) throws CellException
  {
    Logger logger = new Logger();
    if (isRunning(serviceName))
    {
      logger.debug("ServiceManager.stopHTTPService()","Stopping HTTPService "+serviceName);
      cellServiceInstances.get(serviceName).getHttpServer().stop(5);
      cellServiceInstances.remove(serviceName);
    }
    else
    {
      throw new CellException("ServiceManager.stopHTTPService()","Service "+serviceName+" not found.");
    }
  }
  
  public static synchronized void stopHTTPServices()
  {
    Logger logger = new Logger();
    logger.debug("ServiceManager.stopHTTPServices()","Stopping all HTTPServices!");
    Enumeration httpServiceList = cellServiceInstances.keys();
    while(httpServiceList.hasMoreElements())
    {
      String serviceName = (String)httpServiceList.nextElement();
      try
      {
        stopHTTP(serviceName);
      }
      catch (CellException e)
      {
        TissueExceptionHandler.handleGenericException(e,"ServiceManager.stopHTTPServices()","Could not stop HTTPService "+serviceName);
      }
    }
  }
  
  public static synchronized void addHTTPContext(String serviceName,String handlerClassName,ArrayList<String> contexts,Cell cell)
  {
    try
    {
      Logger logger = new Logger();
      if (isLoaded(serviceName))
      {
        HttpServer server = cellServiceInstances.get(serviceName).getHttpServer();
        Class handlerClass = loadClass(handlerClassName);
        CellHTTPHandlerInterface toStartHandler;
        toStartHandler = (CellHTTPHandlerInterface) handlerClass.newInstance();
        toStartHandler.setCell(cell);
        for (String context : contexts) 
        {
          server.createContext(context,toStartHandler);
          logger.debug("ServiceManager.addHTTPContext()","Handler "+handlerClassName+" added context:"+context);
        }
      }
    }
    catch (IllegalAccessException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.addHTTPContext()","IllegalAccessException:");
    }
    catch (InstantiationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.addHTTPContext()","InstantiationException:");
    }
    catch (CellException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceManager.addHTTPContext()","CellException:");
    }
  }
}
