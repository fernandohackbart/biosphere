package org.biosphere.tissue.exceptions;

public class CellException extends Exception {
	private static final long serialVersionUID = 1L;

	public CellException(Throwable throwable) {
		super(throwable);
	}

	public CellException(String message, String module, Throwable throwable) {
		super("CellException at: " + module + " = " + message, throwable);
	}

	public CellException(String module, String message) {
		super("CellException at: " + module + " = " + message);
	}

	public CellException() {
		super();
	}
}
