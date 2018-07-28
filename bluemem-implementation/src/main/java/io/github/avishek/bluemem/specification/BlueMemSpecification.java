package io.github.avishek.bluemem.specification;

import io.github.avishek.bluemem.core.Tupple;

public interface BlueMemSpecification<K, V> {

	public void put(Tupple<K, V> tupple);
	
	public String get(K key);
	
	public void delete(Tupple<String, String> tupple);
	
	public String keys();
	
	public long getTimeStamp();
}
