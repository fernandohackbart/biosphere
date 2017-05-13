package org.biosphere.tissue.services;

import javax.servlet.Servlet;
import org.biosphere.tissue.Cell;

public interface CellServletHandlerInterface extends Servlet {
	public abstract void setCell(Cell cell);
	public abstract void setContentType(String contentType);
	public abstract void setContentEncoding(String contentEncoding);
}
