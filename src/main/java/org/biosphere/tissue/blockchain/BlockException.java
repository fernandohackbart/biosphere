package org.biosphere.tissue.blockchain;

public class BlockException extends Exception {
	private static final long serialVersionUID = 1L;

	public BlockException(String string, Throwable throwable, boolean b, boolean b1) {
		super(string, throwable, b, b1);
	}

	public BlockException(Throwable throwable) {
		super(throwable);
	}

	public BlockException(String message, String module, Throwable throwable) {
		super("BlockException at: " + module + " = " + message, throwable);
	}

	public BlockException(String string, Throwable throwable) {
		super(string, throwable);
	}

	public BlockException(String string) {
		super(string);
	}

	public BlockException() {
		super();
	}
}
