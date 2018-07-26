package io.github.avishek.bluemem.core;

import java.io.Serializable;
import java.util.Map;

public class DataStore<K, V> implements Serializable {

	private static final long serialVersionUID = 5803007559950757682L;
	
	private Map<K, V> STORE;
	
	private Map<K, Integer> DURATION;
	
	public DataStore() {
		
	}

	public DataStore(Map<K, V> STORE) {
		this.STORE = STORE;
	}
	
	public void put(K key, V value) {
		STORE.put(key, value);
	}
	
	public void put(K key, V value, Integer duration) {
		STORE.put(key, value);
		DURATION.put(key, duration);
	}
	
	public V get(K key) {
		return STORE.get(key);
	}
	
	public V delete(K key) {
		return STORE.remove(key);
	}
	
	public String getAllKeys() {
		return STORE.keySet().toString();
	}

}
