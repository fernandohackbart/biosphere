package org.biosphere.tissue.services;

import java.util.ArrayList;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.ServiceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class THREADService extends Thread implements ServiceInterface {
	public THREADService() {
		super();
		logger = LoggerFactory.getLogger(THREADService.class);
	}

	protected Logger logger;
	protected Cell cell;
	ArrayList<ServiceParameter> serviceParameters = new ArrayList<ServiceParameter>();

	@Override
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public void setParameters(ArrayList<ServiceParameter> parameters) {
		serviceParameters = parameters;
	}

	@Override
	public final Object getParameter(String key) {
		Object value = null;
		if (containsParameter(key)) {
			for (ServiceParameter sp : serviceParameters) {
				if (sp.getName().equals(key)) {
					value = sp.getObjectValue();
					break;
				}
			}
		}
		return value;
	}

	public final boolean containsParameter(String key) {
		boolean present = false;
		for (ServiceParameter sp : serviceParameters) {
			if (sp.getName().equals(key)) {
				present = true;
				break;
			}
		}
		return present;
	}
}
