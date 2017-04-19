package org.biosphere.tissue.services;

import java.util.Hashtable;

import org.biosphere.tissue.Cell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class THREADService extends Thread implements ServiceInterface {
	public THREADService() {
		super();
		logger = LoggerFactory.getLogger(THREADService.class);
	}

	protected Logger logger;
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
	public void setParameters(Hashtable<String, Object> parameters) {
		serviceParameters = parameters;
	}

	@Override
	public Object getParameter(String name) {
		return serviceParameters.get(name);
	}

}
