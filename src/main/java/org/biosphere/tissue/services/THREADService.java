package org.biosphere.tissue.services;

import java.util.Hashtable;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.utils.TissueLogger;

public abstract class THREADService extends Thread implements ServiceInterface {
	public THREADService() {
		super();
		logger = new TissueLogger();
	}

	protected TissueLogger logger;
	protected Cell cell;
	protected Hashtable<String, Object> serviceParameters;

	@Override
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParameters(Hashtable parameters) {
		serviceParameters = parameters;
	}

	@Override
	public Object getParameter(String name) {
		return serviceParameters.get(name);
	}

}
