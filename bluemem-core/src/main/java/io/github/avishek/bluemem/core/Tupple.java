package io.github.avishek.bluemem.core;

import java.io.Serializable;

public class Tupple<K, V> implements Serializable{

	private static final long serialVersionUID = -6314669228663465306L;
	
	private K key;
	
	private V value;
	
	private Integer duration;

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Tupple [key=" + key + ", value=" + value + ", duration=" + duration + "]";
	}
}
