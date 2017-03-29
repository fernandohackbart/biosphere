package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;

public class ChainGetFlatChainHandler implements CellHTTPHandlerInterface
{
  private Logger logger;
  
  public ChainGetFlatChainHandler()
  {
    super();
    logger = new Logger();
  }
  
  private Cell cell;

  public void setCell(Cell cell)
  {
    this.cell = cell;
  }

  private Cell getCell()
  {
    return cell;
  }

  @Override
  public void handle(HttpExchange t)
  {
    try
    {
      String partnerCell =  t.getRemoteAddress().getHostName()+":"+t.getRemoteAddress().getPort();
      logger.debug("ChainGetFlatChain.handle()", "Request from: " + partnerCell);
      String response = cell.getChain().toFlat();
      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "application/xml");
      t.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes(), 0, response.getBytes().length);
      os.close();
      //logger.debug("ChainGetFlatChain.handle()", "\n"+response);
      logger.debug("Chain.toFlat()", "############################## DUMP OF THE CHAIN #######################################################");
      logger.debug("Chain.toFlat()", "\n"+cell.getChain().dumpChain());
      logger.debug("Chain.toFlat()", "############################## DUMP OF THE CHAIN #######################################################");

    }
    catch (IOException e)
    {
      ChainExceptionHandler.handleGenericException(e,"ChainGetFlatChain.handle()","IOException:");
    }
    catch (Exception e)
    {
      ChainExceptionHandler.handleGenericException(e,"ChainGetFlatChain.handle()","Exception:");
    }
  }
}
