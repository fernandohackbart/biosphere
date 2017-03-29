package org.biosphere.tissue.handlers;

import com.sun.net.httpserver.HttpHandler;

import org.biosphere.tissue.Cell;

public interface CellHTTPHandlerInterface extends HttpHandler
{
  public abstract void setCell(Cell cell);
}
