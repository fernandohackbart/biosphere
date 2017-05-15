package org.biosphere.tissue.handlers;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.DNA.Service;
import org.biosphere.tissue.tissue.TissueManager;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDefaultHandler extends DefaultHandler  {

	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;
	private Service service;

	public AbstractDefaultHandler() {
		super();
		setContentEncoding(TissueManager.defaultContentEncoding);
		setContentType(TissueManager.defaultContentType);
		logger = LoggerFactory.getLogger(AbstractDefaultHandler.class);
		//Thread.currentThread().setName(AbstractHandler.class.toString());
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	Cell getCell() {
		return cell;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	String getContentType() {
		return this.contentType;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	String getContentEncoding() {
		return this.contentEncoding;
	}
	
	public final Logger getLogger() {
		return logger;
	}

	public final void setLogger(Logger logger) {
		this.logger = logger;
	}

	public final Service getService() {
		return service;
	}

	public final void setService(Service service) {
		this.service = service;
	}
}
