package io.github.avishek.bluemem.core;

import java.io.Serializable;

public class Value<V> implements Serializable{

	private static final long serialVersionUID = -5876579788472488377L;

	private V value;
	
	private long timestamp;

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Value [value=" + value + ", timestamp=" + timestamp + "]";
	}
}
