package org.biosphere.tissue.services;

import java.util.ArrayList;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.ServiceParameter;

public interface ServiceInterface {

	void setParameters(ArrayList<ServiceParameter> parameters);

	Object getParameter(String parameterName);

	public abstract void setCell(Cell cell);

	public abstract Cell getCell();

	public abstract void run();

	public abstract void stop();
}
