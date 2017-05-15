package org.biosphere.tissue.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.protocol.ServiceDiscoveryResponse;
import org.biosphere.tissue.services.ServiceManager;
import org.eclipse.jetty.server.Request;

public class ServiceDefaultHandler extends AbstractDefaultHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String partnerCell = (String) (request.getRemoteHost() + ":" + request.getRemotePort());
		getLogger().debug("ServiceDiscoveryHandler.handle() Not handled request (" + request.getContextPath()
				+ ") from: " + partnerCell);
		String serviceName = ServiceManager.isContextDefined(getCell(), request.getContextPath());
		if (!serviceName.equals(null)) {
			getLogger().debug("ServiceDiscoveryHandler.handle() Context (" + request.getContextPath()
					+ ") handled by service (" + serviceName + ") found in DNA");
			if (ServiceManager.isEnabled(getCell(), serviceName)) {
				getLogger().debug("ServiceDiscoveryHandler.handle() Service (" + serviceName + ") is enabled in DNA");
				ServiceDiscoveryResponse sdr = ServiceManager.discoverService(serviceName,getCell(),getService());
				if (sdr.isRunning()) {
					getLogger().debug("ServiceDiscoveryHandler.handle() Redirecting to remote cell ("+sdr.getCellName()+") ("+sdr.getCellNetworkName()+":"+sdr.getCellServicePort()+") for service (" + serviceName + ") !");
					redirectToCell(request.getContextPath(),sdr,response);
				} else {
					getLogger().debug("ServiceDiscoveryHandler.handle() Starting service (" + serviceName + ") locally!");
					ServiceManager.start(serviceName, getCell());
					ServiceDiscoveryResponse sdrLocal = new ServiceDiscoveryResponse();
					sdrLocal.setCellNetworkName(getCell().getCellNetworkName());
					sdrLocal.setCellName(getCell().getCellName());
					sdrLocal.setCellServicePort((int)getCell().getDna().getService(serviceName).getParameterValue("ServiceListenerPort"));
					getLogger().debug("ServiceDiscoveryHandler.handle() Redirecting to local cell ("+sdrLocal.getCellName()+") ("+sdrLocal.getCellNetworkName()+":"+sdrLocal.getCellServicePort()+") for service (" + serviceName + ") !");
					redirectToCell(request.getContextPath(),sdr,response);
				}
			} else {
				getLogger().debug("ServiceDiscoveryHandler.handle() Service (" + serviceName
						+ ") is disabled in DNA, returning HTTP-404");
				return404(request.getContextPath(), response);
			}
		} else {
			getLogger().warn("ServiceDiscoveryHandler.handle() Context (" + request.getContextPath()
					+ ") not found in DNA! Returning HTTP-404");
			return404(request.getContextPath(), response);
		}
	}

	private void return404(String contextPath, HttpServletResponse response) {
		try {
			String responseString = "<h1>ServiceDiscoveryHandler</h1> HTTP-404 ContextPath: " + contextPath
					+ " not found!";
			response.setContentType(getContentType());
			response.setCharacterEncoding(getContentEncoding());
			response.setContentLength(responseString.getBytes().length);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println(responseString);
			response.flushBuffer();

		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoveryHandler.return404()",
					"Failed to respond to request!");
		}
	}
	
	private void redirectToCell(String contextPath,ServiceDiscoveryResponse sdr,HttpServletResponse response) {
		try {
			response.sendRedirect("https://"+sdr.getCellNetworkName()+":"+sdr.getCellServicePort()+contextPath);
			response.flushBuffer();
		} catch (IOException e) {
			TissueExceptionHandler.handleGenericException(e, "ServiceDiscoveryHandler.redirectToCell()",
					"Failed to respond to request!");
		}
	}

}
