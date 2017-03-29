package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.services.ServiceManager;

public class ServiceStopHandler implements CellHTTPHandlerInterface
{
  public ServiceStopHandler()
  {
    logger = new Logger();
  }
  
  private Logger logger;
  private Cell cell;

  public void setCell(Cell cell)
  {
    this.cell = cell;
  }
  
  private String getRequestAsString(InputStream input)  throws IOException
  {
    BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
    return buffer.lines().collect(Collectors.joining("\n"));
  }
  
  @Override
  public void handle(HttpExchange t)
  {
    try
    {
      String partnerCell =  t.getRemoteAddress().getHostName()+":"+t.getRemoteAddress().getPort();
      String request = getRequestAsString(t.getRequestBody());
      logger.debug("ServiceStopHandler.handle()", "Request from: " + partnerCell);
      String response = "<h1>ServiceStopHandler.handle()</h1> Cell stop request from: "+partnerCell;
      try 
      {
        ServiceManager.stop("THREAD",request);  
      }
      catch (CellException e)
      {
        TissueExceptionHandler.handleGenericException(e,"ServiceStopHandler.handle()","Failed to stop service:");
        response = "<h1>ServiceStopHandler.handle()</h1> Service stop request from: "+partnerCell+" Exception: "+e.getMessage();
      }
  
      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "text/html");
      t.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes(), 0, response.getBytes().length);
      os.close();
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"ServiceStopHandler.handle()","IOException:");
    }
  }
}
