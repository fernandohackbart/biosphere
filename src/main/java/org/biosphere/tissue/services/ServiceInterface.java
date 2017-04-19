package org.biosphere.tissue.services;

import java.util.Hashtable;
import org.biosphere.tissue.Cell;

public interface ServiceInterface {
	Hashtable<String, Object> serviceParameters = new Hashtable<String, Object>();

	void setParameters(Hashtable<String, Object> parameters);

	Object getParameter(String parameterName);

	public abstract void setCell(Cell cell);

	public abstract Cell getCell();

	public abstract void run();

	public abstract void stop();
}
