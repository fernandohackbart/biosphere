package org.biosphere.tissue.utils;

import java.util.Date;

public class Logger {
	public Logger() {
		super();
	}

	public void warn(String module, String message) {
		System.out.println(new Date() + " WARN            " + module + " : " + message);
	}

	public void info(String module, String message) {
		System.out.println(new Date() + " INFO            " + module + " : " + message);
	}

	public void trace(String module, String message) {
		System.out.println(new Date() + " INFO            " + module + " : " + message);
	}

	public void error(String module, String message) {
		System.out.println(new Date() + " ERROR           " + module + " : " + message);
	}

	public void debug(String module, String message) {
		System.out.println(new Date() + " DEBUG           " + module + " : " + message);
	}

	public void debugAddBlock(String module, String message) {
		System.out.println(new Date() + " DEBUG ADD BLOCK " + module + " : " + message);
	}

	public void debugAppBlock(String module, String message) {
		System.out.println(new Date() + " DEBUG APP BLOCK " + module + " : " + message);
	}

	public void exception(String module, String message) {
		System.out.println(new Date() + " EXCEPTION       " + module + " : " + message);
	}

	public void debugSSL(String module, String message) {
		System.out.println(new Date() + " SSL             " + module + " : " + message);
	}
}
