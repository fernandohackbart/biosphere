package org.biosphere.tissue.handlers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.utils.Logger;

public class ChainGetImageChainHandler extends HttpServlet implements CellJettyHandlerInterface {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	private Cell cell;
	private String contentType;

	public ChainGetImageChainHandler() {
		super();
		logger = new Logger();
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

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String partnerCell = request.getRemoteHost() + ":" + request.getRemotePort();
		logger.debug("ChainGetImageChain.handle()", "Request from: " + partnerCell);
		byte[] responseBytes = generateRandomImage();
		// byte[] response = generateGraphImage(cell.getChain().toFlat());
		response.setContentType(getContentType());
		response.setContentLength(responseBytes.length);
		response.setStatus(HttpServletResponse.SC_OK);
		OutputStream os = response.getOutputStream();
		os.write(responseBytes, 0, responseBytes.length);
		os.close();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	private byte[] generateRandomImage() throws IOException {
		int width = 640;
		int height = 320;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int a = (int) (Math.random() * 256); // alpha
				int r = (int) (Math.random() * 256); // red
				int g = (int) (Math.random() * 256); // green
				int b = (int) (Math.random() * 256); // blue

				int p = (a << 24) | (r << 16) | (g << 8) | b; // pixel

				img.setRGB(x, y, p);
			}
		}
		ByteArrayOutputStream ios = new ByteArrayOutputStream();
		ImageIO.write(img, "png", ios);
		return ios.toByteArray();
	}

}
