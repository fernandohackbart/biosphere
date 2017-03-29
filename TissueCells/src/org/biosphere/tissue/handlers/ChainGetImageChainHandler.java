package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.blockchain.ChainExceptionHandler;
import org.biosphere.tissue.utils.Logger;


public class ChainGetImageChainHandler implements CellHTTPHandlerInterface
{
  private Logger logger;

  public ChainGetImageChainHandler()
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
      String partnerCell = t.getRemoteAddress().getHostName() + ":" + t.getRemoteAddress().getPort();
      logger.debug("ChainGetImageChain.handle()", "Request from: " + partnerCell);

      byte[] response = generateRandomImage();
      //byte[] response = generateGraphImage(cell.getChain().toFlat());

      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "image/png");
      t.sendResponseHeaders(200, response.length);
      OutputStream os = t.getResponseBody();
      os.write(response, 0, response.length);
      os.close();
    }
    catch (IOException e)
    {
      ChainExceptionHandler.handleGenericException(e, "ChainGetFlatChain.handle()", "IOException:");
    }
    catch (Exception e)
    {
      ChainExceptionHandler.handleGenericException(e, "ChainGetFlatChain.handle()", "Exception:");
    }
  }

  private byte[] generateRandomImage() throws IOException
  {
    int width = 640;
    int height = 320;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        int a = (int) (Math.random() * 256); //alpha
        int r = (int) (Math.random() * 256); //red
        int g = (int) (Math.random() * 256); //green
        int b = (int) (Math.random() * 256); //blue

        int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel

        img.setRGB(x, y, p);
      }
    }
    ByteArrayOutputStream ios = new ByteArrayOutputStream();
    ImageIO.write(img, "png", ios);
    return ios.toByteArray();
  }

}
