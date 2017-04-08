package org.biosphere.tissue.DNA;

public class DNAEntry {
	public DNAEntry() {
		super();
	}

	String entryName;
	String entryType;
	Object entryValue;

	public synchronized void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public synchronized String getEntryName() {
		return entryName;
	}

	public synchronized void setEntryType(String entruType) {
		this.entryType = entruType;
	}

	public synchronized String getEntryType() {
		return entryType;
	}

	public synchronized void setEntryValue(Object entryValue) {
		this.entryValue = entryValue;
	}

	public synchronized Object getEntryValue() {
		return entryValue;
	}
}
