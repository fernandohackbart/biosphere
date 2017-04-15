package org.biosphere.tissue.handlers;

import javax.servlet.Servlet;
import org.biosphere.tissue.Cell;

public interface CellJettyHandlerInterface extends Servlet {
	public abstract void setCell(Cell cell);
	public abstract void setContentType(String contentType);
}
