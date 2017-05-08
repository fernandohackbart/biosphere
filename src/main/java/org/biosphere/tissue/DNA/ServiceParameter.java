package org.biosphere.tissue.DNA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;

import org.bouncycastle.util.encoders.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceParameter {

	@JsonProperty("name")
	String name;
	@JsonProperty("value")
	String value;

	@JsonProperty("name")
	public final String getName() {
		return name;
	}
	@JsonProperty("name")
	public final void setName(String name) {
		this.name = name;
	}
	@JsonProperty("value")
	final String getValue() {
		return value;
	}
	@JsonProperty("value")
	final void setValue(String value) {
		this.value = value;
	}
	@JsonIgnore
	public void setObjectValue(Object value) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(value);
		so.flush();
		setValue(Base64.toBase64String(bo.toByteArray()));
	}
	@JsonIgnore
	public Object getObjectValue() {
		Object value = null;
		try
		{
			byte b[] = Base64.decode(getValue());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			value = si.readObject();
		}
		catch (ClassNotFoundException | IOException e)
		{
			TissueExceptionHandler.handleGenericException(e, "ServiceParameter.getObjectValue()", e.getLocalizedMessage());
		}
		return value;
	}
}
