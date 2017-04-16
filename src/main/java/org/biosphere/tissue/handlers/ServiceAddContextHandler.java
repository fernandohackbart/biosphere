package org.biosphere.tissue.handlers;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.biosphere.tissue.Cell;
import org.biosphere.tissue.services.ServiceManager;
import org.biosphere.tissue.services.ServletHandlerDefinition;
import org.biosphere.tissue.utils.RequestUtils;
import org.biosphere.tissue.protocol.ServiceServletContext;
import org.biosphere.tissue.protocol.ServiceServletContextURI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceAddContextHandler extends HttpServlet implements CellServletHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;
	private String contentEncoding;
	
	public ServiceAddContextHandler() {
		logger = LoggerFactory.getLogger(ServiceAddContextHandler.class);
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
	private Cell getCell() {
		return cell;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String getContentType()
	{
		return this.contentType;
	}	
	
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	
	private String getContentEncoding()
	{
		return this.contentEncoding;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		String requestPayload = RequestUtils.getRequestAsString(request.getInputStream());
		
        ObjectMapper mapper = new ObjectMapper();
        ServiceServletContext ssc = mapper.readValue(requestPayload.getBytes(),ServiceServletContext.class);
        
		ServletHandlerDefinition cellSampleServiceSHD = new ServletHandlerDefinition();
		cellSampleServiceSHD.setClassName(ssc.getClassName());
		cellSampleServiceSHD.setContentType(ssc.getContentType());
		ArrayList<String> chainParseChainContexts = new ArrayList<String>();
		for (ServiceServletContextURI context : ssc.getContextURIs() )
		{
			chainParseChainContexts.add(context.getContextURI());	
		}
		cellSampleServiceSHD.setContexts(chainParseChainContexts);
		ServiceManager.addServletContext(ssc.getServiceName(),cellSampleServiceSHD, cell);

		logger.debug("ServiceContextManagerHandler.doPost() Request from: " + partnerCell);
		String responseString = "<h1>ServiceContextManagerHandler.doPost()</h> "+ ssc.getClassName()+ " from " + partnerCell;
     	response.setContentType(getContentType());
     	response.setCharacterEncoding(getContentEncoding());
		response.setContentLength(responseString.getBytes().length);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(responseString);	
		response.flushBuffer();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
