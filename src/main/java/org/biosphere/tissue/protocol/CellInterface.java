package org.biosphere.tissue.protocol;

public class CellInterface {
	public CellInterface() {
		super();
	}

	public CellInterface(String cellName, String cellNetworkName, int port) {
		super();
		setCellName(cellName);
		setCellNetworkName(cellNetworkName);
		setPort(port);
	}

	private String cellName;
	private String cellNetworkName;
	private int port;

	public final void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public final String getCellName() {
		return cellName;
	}

	public final void setCellNetworkName(String cellNetworkName) {
		this.cellNetworkName = cellNetworkName;
	}

	public final String getCellNetworkName() {
		return cellNetworkName;
	}

	public final void setPort(int port) {
		this.port = port;
	}

	public final int getPort() {
		return port;
	}
}
