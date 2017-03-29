package org.biosphere.tissue.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;

import java.nio.charset.StandardCharsets;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;

public class CellAnnounceListener extends THREADService
{
  public CellAnnounceListener()
  {
    super();
  }
  
  MulticastSocket socket=null;
  
  private String getResponseAsString(InputStream input)  throws IOException
  {
    BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
    return buffer.lines().collect(Collectors.joining("\n")); 
  }
  
  @Override
  public void interrupt()
  {
    super.interrupt();  
    socket.close();
  }
  
  @Override
  public void run()
  {
    boolean keepListening = true;
    try
    {
      socket = new MulticastSocket(new Integer((String)getParameter("AnnouncePort")));
      InetAddress address = InetAddress.getByName((String)getParameter("AnnounceAddress"));
      logger.debug("CellAnnounceListener.run()","listening at "+(String)getParameter("AnnounceAddress")+":"+getParameter("AnnouncePort")+"!");
      socket.joinGroup(address);
      DatagramPacket packet;
      while (keepListening)
      {
        byte[] buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String receivedPayload = new String(packet.getData(), 0, packet.getLength());
        logger.debug("CellAnnounceListener.run()","received request from "+receivedPayload+" adopting cell!");
        adoptCell(receivedPayload);
      }
      socket.leaveGroup(address);
      socket.close();          
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.run()","IOException:");
      keepListening=false;
    }
    catch (NullPointerException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.run()","NullPointerException:");
      keepListening=false;
    }
  }
  
  private void adoptCell(String cellAddress)
  {
    try
    {
      logger.debug("CellAnnounceListener.adoptCell()","Adopting: "+cellAddress);   
      String requestWelcome="WLCM:"+cell.getCellDNA().getTissueName()+":"+cell.getCellName()+"\n"+cell.getCellCertificate();
      URL urlWelcome = new URL("https://"+cellAddress.split(":")[0]+":"+cellAddress.split(":")[1]+"/org/biosphere/tissue/welcome");
      logger.debug("CellAnnounceListener.adoptCell()","Contacting: "+urlWelcome.getProtocol()+"://"+urlWelcome.getHost()+":"+urlWelcome.getPort()+"/org/biosphere/tissue/welcome");  
      
      CellManager.setRelaxedSSLSocketFactory(cell);
      
      HttpsURLConnection connWelcome = (HttpsURLConnection)urlWelcome.openConnection();
      connWelcome.setRequestMethod("POST");
      connWelcome.setDoOutput(true);
      connWelcome.setInstanceFollowRedirects(false);
      connWelcome.setRequestProperty("Content-Type","application/xml"); 
      connWelcome.setRequestProperty("charset", "utf-8");
      connWelcome.setRequestProperty("Content-Length",""+requestWelcome.getBytes(StandardCharsets.UTF_8).length);
      connWelcome.setUseCaches(false);
      
      DataOutputStream wrWelcome = new DataOutputStream(connWelcome.getOutputStream()); 
      wrWelcome.write(requestWelcome.getBytes());
      connWelcome.connect();
      String responsePayload = getResponseAsString(connWelcome.getInputStream());
      connWelcome.disconnect();
      
      CellManager.setDefaultSSLSocketFactory(cell);
      
      String responseWelcome = responsePayload.substring(0, responsePayload.indexOf("\n"));
      logger.debug("CellAnnounceListener.adoptCell()","Welcome response: "+responseWelcome);  
      
      if (responseWelcome.startsWith("GRTS:"))
      {
        String responseCellName = responseWelcome.split(":")[1];
        String responseCert = responsePayload.substring(responsePayload.indexOf("\n")+1);
        try
        {
          CellManager.addCellTrustKeystore(responseCellName,responseCert, cell);
        }
        catch (CertificateEncodingException | KeyStoreException e)
        {
          TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.adoptCell()","CellManager.addCellTrustKeystore:");
        }
        catch (CertificateException e)
        {
          TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.adoptCell()","CellManager.addCellTrustKeystore:");
        }
                            
        String requestJoin = "https://"+cell.getCellNetworkName()+":"+cell.getTissuePort()+"/org/biosphere/tissue/DNA/DNACore.xml\n"+cell.getCellCertificate();
        URL urlJoin = new URL("https://"+cellAddress.split(":")[0]+":"+cellAddress.split(":")[1]+"/org/biosphere/tissue/join");
        logger.debug("CellAnnounceListener.adoptCell()","Sending DNACore URL to: "+urlJoin.getProtocol()+"://"+urlJoin.getHost()+":"+urlJoin.getPort()+"/org/biosphere/tissue/join");  
        HttpsURLConnection connJoin = (HttpsURLConnection)urlJoin.openConnection();
        connJoin.setRequestMethod("POST");
        connJoin.setDoOutput(true);
        connJoin.setInstanceFollowRedirects(false);
        connJoin.setRequestProperty("Content-Type","application/xml"); 
        connJoin.setRequestProperty("charset", "utf-8");
        connJoin.setRequestProperty("Content-Length",""+requestJoin.getBytes(StandardCharsets.UTF_8).length);
        connJoin.setUseCaches(false); 
        DataOutputStream wrJoin = new DataOutputStream(connJoin.getOutputStream()); 
        wrJoin.write(requestJoin.getBytes());
        //TODO send all the possible interfaces
        connJoin.connect();
        String responseJoin = getResponseAsString(connJoin.getInputStream());
        connJoin.disconnect();
        logger.debug("CellAnnounceListener.adoptCell()","Join response: "+responseJoin);  
        
        //TODO send the Chain from this point
        String requestChain = cell.getChain().toFlat();
        URL urlChain = new URL("https://"+cellAddress.split(":")[0]+":"+cellAddress.split(":")[1]+"/org/biosphere/cell/chain/parse/chain");
        logger.debug("CellAnnounceListener.adoptCell()","Sending Chain to: "+urlJoin.getProtocol()+"://"+urlJoin.getHost()+":"+urlJoin.getPort()+"/org/biosphere/cell/chain/parse/chain");  
        HttpsURLConnection connChain = (HttpsURLConnection)urlChain.openConnection();
        connChain.setRequestMethod("POST");
        connChain.setDoOutput(true);
        connChain.setInstanceFollowRedirects(false);
        connChain.setRequestProperty("Content-Type","application/xml"); 
        connChain.setRequestProperty("charset", "utf-8");
        connChain.setRequestProperty("Content-Length",""+requestJoin.getBytes(StandardCharsets.UTF_8).length);
        connChain.setUseCaches(false); 
        DataOutputStream wrChain = new DataOutputStream(connChain.getOutputStream()); 
        wrChain.write(requestChain.getBytes());
        connJoin.connect();
        String responseChain = getResponseAsString(connChain.getInputStream());
        connJoin.disconnect();
        logger.debug("CellAnnounceListener.adoptCell()","Chain send response: "+responseChain);  
        
        logger.debug("CellAnnounceListener.adoptCell()","Adding adopted cell to the local DNA!");
        cell.getCellDNA().addCell(responseCellName, responseCert, cellAddress.split(":")[0], Integer.parseInt(cellAddress.split(":")[1]));    
        
      }
    }
    catch (UnknownHostException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.adoptCell()","UnknownHostException:");
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"CellAnnounceListener.adoptCell()","IOException:");
    }
  }
  
}
