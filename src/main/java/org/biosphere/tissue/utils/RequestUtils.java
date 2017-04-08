package org.biosphere.tissue.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.stream.Collectors;

public class RequestUtils {
	public RequestUtils() {
		super();
	}

	public static String getRequestAsString(InputStream input) throws IOException {
		BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
		return buffer.lines().collect(Collectors.joining("\n"));
	}
}
