package io.github.avishek.bluemem.core;

public class Value<V> {

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
