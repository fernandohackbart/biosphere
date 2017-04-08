package org.biosphere.tissue.services;

import java.util.Hashtable;
import org.biosphere.tissue.Cell;

public interface ServiceInterface {
	Hashtable serviceParameters = new Hashtable();

	void setParameters(Hashtable parameters);

	Object getParameter(String parameterName);

	public abstract void setCell(Cell cell);

	public abstract Cell getCell();

	public abstract void run();

	public abstract void stop();
}
