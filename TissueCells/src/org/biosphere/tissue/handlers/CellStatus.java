package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.List;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.CellInterface;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.KeystoreManager;
import org.biosphere.tissue.utils.Logger;
import org.biosphere.tissue.utils.RequestUtils;

public class CellStatus implements CellHTTPHandlerInterface
{
  private Logger logger;
  
  public CellStatus()
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
      logger.debug("CellStatus.handle()", "##############################################################################");
      logger.debug("CellStatus.handle()", "Cell "+cell.getCellName()+" request from: " + partnerCell);
      String request = RequestUtils.getRequestAsString(t.getRequestBody());
      StringBuffer response = new StringBuffer();
      response.append("##############################################################################\n");
      response.append("Cell: "+getCell().getCellName()+"\n");
      response.append("Cell network name: "+getCell().getCellNetworkName()+"\n");
      response.append("Cell tissue port: "+getCell().getTissuePort()+"\n");
      response.append("Cell tissue certificate: \n"+getCell().getCellCertificate());
      response.append("##############################################################################\n");
      response.append("Tissue name: "+getCell().getCellDNA().getTissueName()+"\n");
      response.append("Tissue size: "+getCell().getCellDNA().getTissueSize()+"\n");
      response.append("##############################################################################\n");
      response.append("Tissue cells: \n");
      List<CellInterface> celIterfaces = getCell().getCellDNA().getTissueCellsInterfaces();
      Iterator cellsIfIterator = celIterfaces.iterator();
      while (cellsIfIterator.hasNext())
      {
        CellInterface cellInterface = (CellInterface) cellsIfIterator.next();
        response.append("  Tissue cell: "+cellInterface.getCellName()+"("+cellInterface.getCellNetworkName()+":"+cellInterface.getPort()+")\n");
      }
      response.append("##############################################################################\n");
      response.append("Chain dump: "+getCell().getChain().dumpChain()+"\n");
      response.append("##############################################################################\n");
      response.append("Keystore dump: \n"+new KeystoreManager().dumpKeystore(getCell().getCellKeystore(), getCell().getCellKeystorePWD())+"\n");
      response.append("##############################################################################\n");
      response.append("Tissue DNA: \n"+getCell().getCellDNA().getDNACoreAsPrettyString()+"\n");
      response.append("##############################################################################\n");
      
      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "text/plain");
      t.sendResponseHeaders(200, response.toString().getBytes().length);
      OutputStream os = t.getResponseBody();
      os.write(response.toString().getBytes(), 0, response.toString().getBytes().length);
      os.close();
      logger.debug("CellStatus.handle()","Response:"+ response);
      
    }
    catch (IOException e)
    {
      ChainExceptionHandler.handleGenericException(e,"CellStatus.handle()","IOException:");
    }
    catch (Exception e)
    {
      ChainExceptionHandler.handleGenericException(e,"CellStatus.handle()","Exception:");
    }
  }
}
