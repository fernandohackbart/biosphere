package org.biosphere.tissue.cell;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigInteger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.Tissue;
import org.biosphere.tissue.DNA.Tissueservicetype;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.services.ServiceDefinition;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.KeystoreManager;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RelaxedTrustManager;

import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

public class CellManager
{
  public CellManager()
  {
    super();
  }
  
  public final static void setupCell(Cell cell)
  {
    cell.setCellName(generateCellName());
    cell.setCellNetworkName(getCellNetworkName());
    cell.setTissueMember(false);
    cell.setTissuePort(TissueManager.defaultTissuePort);
    cell.setCellKeystorePWD(generateCellKeystorePWD());
  }

  private static String generateCellName()
  {
    return generateCellRandomName();
    //return generateCellNetName();
  }

  private static String generateCellRandomName()
  {
    Logger logger = new Logger();
    String cellName = UUID.randomUUID().toString();
    logger.info("CellManager.generateCellName()","Cell name: "+cellName);
    return cellName;
  }
  
  private static String generateCellNetName()
  {
    Logger logger = new Logger();
    String cellName = "NotDefined!";
    try
    {
      String networkName = InetAddress.getLocalHost().getHostName();
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
      Date now = new Date();
      String strDate = sdfDate.format(now);
      cellName = networkName+"-"+strDate;
      logger.info("CellManager.generateCellName()","Cell name: "+cellName);
    }
    catch (UnknownHostException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.generateCellName()","Failed to generate Cell name due to UnknowHost Exception!");
    }
    return cellName;
  }
  
  public static String getCellNetworkName()
  {
    Logger logger = new Logger();
    String cellName = "localhost";
    try
    {
      String networkName = InetAddress.getLocalHost().getHostName();
      cellName = networkName;
      logger.info("CellManager.generateCellName()","Cell network name: "+cellName);
    }
    catch (UnknownHostException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.gatCellNetworkName()","Failed to generate Cell name due to UnknowHost Exception!");
    }
    return cellName;
  }
  
  public final static void stopCell()
  {
    try
    {
      ServiceManager.stop("THREAD","CellMonitor");
    }
    catch (CellException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.stopCell()","Failed to stop Cell.");
    }
  }
  
  public final static ServiceDefinition getCellMonitorDefinition()
  {
    ServiceDefinition sdCellMonitor = new ServiceDefinition();
    sdCellMonitor.setServiceDefinitionName("CellMonitor");
    sdCellMonitor.setServiceDefinitionType("THREAD");
    sdCellMonitor.setServiceDefinitionVersion("0.1");
    sdCellMonitor.setServiceDefinitionDaemon(false);
    sdCellMonitor.setServiceDefinitionClass("org.biosphere.tissue.services.CellMonitor");
    sdCellMonitor.addServiceDefinitionParameter("Interval",TissueManager.monitorInterval);
    return sdCellMonitor;
  }
  
  public final static ServiceDefinition getCellAnnounceListenerDefinition()
  {
    ServiceDefinition sdCellAnnounceListener = new ServiceDefinition();
    sdCellAnnounceListener.setServiceDefinitionName("CellAnnounceListener");
    sdCellAnnounceListener.setServiceDefinitionType("THREAD");
    sdCellAnnounceListener.setServiceDefinitionVersion("0.1");
    sdCellAnnounceListener.setServiceDefinitionClass("org.biosphere.tissue.services.CellAnnounceListener");
    sdCellAnnounceListener.addServiceDefinitionParameter("AnnouncePort",TissueManager.announcePort);
    sdCellAnnounceListener.addServiceDefinitionParameter("AnnounceAddress",TissueManager.announceAddress);
    return sdCellAnnounceListener;
  }

  public final static ServiceDefinition getCellACSDefinition()
  {
    ServiceDefinition sdCellACS = new ServiceDefinition();
    sdCellACS.setServiceDefinitionName("CellAdministrationConsole");
    sdCellACS.setServiceDefinitionType("THREAD");
    sdCellACS.setServiceDefinitionVersion("0.1");
    sdCellACS.setServiceDefinitionClass("org.biosphere.tissue.services.CellAdministrationConsole");
    sdCellACS.addServiceDefinitionParameter("ListenPort",TissueManager.announcePort);
    return sdCellACS;
  }  
  
  public final static ServiceDefinition getCellTissueListenerDefinition()
  {
    // all those paramters should be placed inside the DNA and the consumed from there when starting the service 
    Hashtable<String,ArrayList> cellTissueListenerHandlers = new Hashtable<String,ArrayList>();

    ArrayList<String> cellDNACoreContexts = new ArrayList<String>();
    cellDNACoreContexts.add("/org/biosphere/tissue/DNA/DNACore.xml");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellDNACoreHandler",cellDNACoreContexts);
    
    ArrayList<String> cellTissueWelcomeContexts = new ArrayList<String>();
    cellTissueWelcomeContexts.add("/org/biosphere/tissue/welcome");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellTissueWelcomeHandler",cellTissueWelcomeContexts);
    
    ArrayList<String> cellTissueJoinContexts = new ArrayList<String>();
    cellTissueJoinContexts.add("/org/biosphere/tissue/join");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellTissueJoinHandler",cellTissueJoinContexts); 
    
    ArrayList<String> cellDNASchemaContexts = new ArrayList<String>();
    cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueCell-1.0.xsd");
    cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueCellInterface-1.0.xsd");
    cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueDNA-1.0.xsd");
    cellDNASchemaContexts.add("/org/biosphere/tissue/DNA/TissueService-1.0.xsd");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellDNASchemaHandler",cellDNASchemaContexts); 
    
    ArrayList<String> cellStopContexts = new ArrayList<String>();
    cellStopContexts.add("/org/biosphere/cell/stop");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellStopHandler",cellStopContexts); 

    ArrayList<String> cellHTTPContextAddContexts = new ArrayList<String>();
    cellHTTPContextAddContexts.add("/org/biosphere/cell/http/context/add");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellHTTPContextManagerHandler",cellHTTPContextAddContexts); 
    
    ArrayList<String> serviceStopContexts = new ArrayList<String>();
    serviceStopContexts.add("/org/biosphere/cell/service/stop");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ServiceStopHandler",serviceStopContexts); 
    
    ArrayList<String> httpServiceStopContexts = new ArrayList<String>();
    httpServiceStopContexts.add("/org/biosphere/cell/httpservice/stop");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.HTTPServiceStopHandler",httpServiceStopContexts); 
    
    ArrayList<String> cellStatusContexts = new ArrayList<String>();
    cellStatusContexts.add("/org/biosphere/cell/status");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.CellStatus",cellStatusContexts); 
      
    ServiceDefinition sdCellTissueListener = new ServiceDefinition();
    sdCellTissueListener.setServiceDefinitionName("CellTissueListener");
    sdCellTissueListener.setServiceDefinitionType("HTTP");
    sdCellTissueListener.setServiceDefinitionVersion("0.1");
    sdCellTissueListener.setServiceDefinitionClass("com.sun.net.httpserver.HttpServer");
    sdCellTissueListener.addServiceDefinitionParameter("Handlers",cellTissueListenerHandlers);
    sdCellTissueListener.addServiceDefinitionParameter("DefaultHTTPPort",TissueManager.defaultTissuePort);
    
    ArrayList<String> chainAddBlockContexts = new ArrayList<String>();
    chainAddBlockContexts.add("/org/biosphere/cell/chain/add/block");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ChainAddBlockHandler",chainAddBlockContexts); 
    
    ArrayList<String> chainAppendBlockContexts = new ArrayList<String>();
    chainAppendBlockContexts.add("/org/biosphere/cell/chain/append/block");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ChainAppendBlockHandler",chainAppendBlockContexts); 
    
    ArrayList<String> chainParseChainContexts = new ArrayList<String>();
    chainParseChainContexts.add("/org/biosphere/cell/chain/parse/chain");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ChainParseChainHandler",chainParseChainContexts); 
    
    ArrayList<String> chainGetChainContexts = new ArrayList<String>();
    chainGetChainContexts.add("/org/biosphere/cell/chain/get/chain");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ChainGetFlatChainHandler",chainGetChainContexts); 
   
    ArrayList<String> chainGetImageChainContexts = new ArrayList<String>();
    chainGetImageChainContexts.add("/org/biosphere/cell/chain/get/chainimage");
    cellTissueListenerHandlers.put("org.biosphere.tissue.handlers.ChainGetImageChainHandler",chainGetImageChainContexts); 
            
    return sdCellTissueListener;
  }
  
  public final static ServiceDefinition getCellServiceListenerDefinition()
  {
    // all those paramters should be placed inside the DNA and the consumed from there when starting the service 
    Hashtable<String,ArrayList> cellServiceListenerHandlers = new Hashtable<String,ArrayList>();

    ArrayList<String> cellDNACoreContexts = new ArrayList<String>();
    cellDNACoreContexts.add("/");
    cellServiceListenerHandlers.put("org.biosphere.tissue.handlers.CellServiceInstantiationHandler",cellDNACoreContexts);
    
    ServiceDefinition sdCellTissueListener = new ServiceDefinition();
    sdCellTissueListener.setServiceDefinitionName("CellServiceListener");
    sdCellTissueListener.setServiceDefinitionType("HTTP");
    sdCellTissueListener.setServiceDefinitionVersion("0.1");
    sdCellTissueListener.setServiceDefinitionClass("com.sun.net.httpserver.HttpServer");
    sdCellTissueListener.addServiceDefinitionParameter("Handlers",cellServiceListenerHandlers);
    sdCellTissueListener.addServiceDefinitionParameter("DefaultHTTPPort",TissueManager.defaultTissuePort+TissueManager.portJumpFactor);
    return sdCellTissueListener;
  }
   
  public static final void startTissueListenerService(Cell cell) throws CellException
  {
    cell.setTissuePort(ServiceManager.startServiceDefinition(getCellTissueListenerDefinition(),cell)); 
  }
  
  public static final void loadServicesDNA(Cell cell)
  {
    Logger logger = new Logger();
    logger.debug("CellManager.addServicesDNA()","Adding CellMonitor definition to DNA");
    cell.getCellDNA().addService(getCellMonitorDefinition());
    logger.debug("CellManager.addServicesDNA()","Adding CellAnnounceListener definition to DNA");
    cell.getCellDNA().addService(getCellAnnounceListenerDefinition()); 
    logger.debug("CellManager.addServicesDNA()","Adding CellTissueListener definition to DNA");
    cell.getCellDNA().addService(getCellTissueListenerDefinition());
    //logger.debug("CellManager.addServicesDNA()","Adding CellACS definition to DNA");
    //cell.getCellDNA().addService(getCellACSDefinition());
    logger.debug("CellManager.addServicesDNA()","Adding CellServiceListener definition to DNA");
    cell.getCellDNA().addService(getCellServiceListenerDefinition());
  }
  
  public static final void startServicesDNA(Cell cell)
  {
    Logger logger = new Logger();
    Tissue.Tissueservices services = cell.getCellDNA().getServices();
    Iterator servicesIterator = services.getTissueservice().iterator();
    while(servicesIterator.hasNext())
    {
      Tissueservicetype service = (Tissueservicetype)servicesIterator.next();
      logger.debug("CellManager.startServicesDNA()","Starting "+service.getServicename()+" from DNA");
      ServiceManager.start(service.getServicename(),cell); 
    }
  }
  
  public static final String generateCellKeystorePWD()
  {  
    SecureRandom random = new SecureRandom();
    return new BigInteger(130,random).toString(32);
  }
  
  public static final KeyStore generateCellKeystore(Cell cell) throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException, CertificateException, KeyStoreException
  {
    KeystoreManager kg = new KeystoreManager();
    KeyStore ks = kg.getKeyStore(cell.getCellName(),cell.getCellNetworkName(),cell.getCellKeystorePWD());
    //kg.dumpKeystore(ks,cell.getCellKeystorePWD());    
    return ks;
  }
  
  public static final void setDefaultSSLSocketFactory(Cell cell)
  {
    Logger logger = new Logger();
    logger.info("CellManager.setDefaultSSLSocketFactory()","CellName:"+cell.getCellName());
    try
    {
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(cell.getCellKeystore());
      TrustManager[] trustAllCerts = tmf.getTrustManagers();
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());      
    }
    catch (NoSuchAlgorithmException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.setDefaultSSLSocketFactory()","NoSuchAlgorithmException:");
    }
    catch (KeyStoreException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.setDefaultSSLSocketFactory()","KeyStoreException:");
    }
    catch (KeyManagementException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.setDefaultSSLSocketFactory()","KeyManagementException:");
    }
  }

  public static final void setRelaxedSSLSocketFactory(Cell cell)
  {
    Logger logger = new Logger();
    logger.info("CellManager.setRelaxedSSLSocketFactory()","CellName:"+cell.getCellName());
    try
    {
      TrustManager[] trustAllCerts = new TrustManager[1];
      trustAllCerts[0]=new RelaxedTrustManager();
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());     
    }
    catch (NoSuchAlgorithmException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.setRelaxedSSLSocketFactory()","NoSuchAlgorithmException:");
    }
    catch (KeyManagementException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellManager.setRelaxedSSLSocketFactory()","KeyManagementException:");
    }
  }
  
  public static final String getCellCertificateFromKeystore(Cell cell) throws KeyStoreException, CertificateEncodingException, IOException
  {
    Logger logger = new Logger();
    KeyStore ks = cell.getCellKeystore();
    logger.info("CellManager.getCellCertificateFromKeystore()","CellName:"+cell.getCellName());
    Certificate cert = ks.getCertificate(cell.getCellName());
    StringWriter sw = new StringWriter();
    PemWriter pw = new PemWriter(sw);
    pw.writeObject(new JcaMiscPEMGenerator(cert));
    pw.flush();
    String pemEncodedCert = sw.toString();
    logger.debug("CellManager.getCellCertificateFromKeystore()","\n"+pemEncodedCert);
    return pemEncodedCert;
  }
  
  public static final void addCellTrustKeystore(String cellName,String certPem,Cell cell) throws KeyStoreException, CertificateEncodingException, IOException, CertificateException
  {
    Logger logger = new Logger();
    logger.info("CellManager.addCellTrustKeystore()","CellName:"+cell.getCellName()+" remote CellName:"+cellName);
    logger.info("CellManager.addCellTrustKeystore()","Certificate:\n"+certPem);
    PemReader pr = new PemReader(new StringReader(certPem)); 
    PemObject pem = pr.readPemObject();
    CertificateFactory cf = CertificateFactory.getInstance("X509");
    Certificate cert = cf.generateCertificate(new ByteArrayInputStream(pem.getContent()));
    KeyStore ks = cell.getCellKeystore();
    ks.setCertificateEntry(cellName,cert);
    CellManager.setDefaultSSLSocketFactory(cell);
  }
}
