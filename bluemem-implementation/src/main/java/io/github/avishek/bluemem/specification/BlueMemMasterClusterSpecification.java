package io.github.avishek.bluemem.specification;

import java.io.IOException;
import java.net.MalformedURLException;

import io.github.avishek.bluemem.core.Tupple;

public interface BlueMemMasterClusterSpecification<K, V> {

	long getTimestamp() throws MalformedURLException, IOException;
	
	void traceNodes();
	
	void callForPut(Tupple<K, V> tupple);
	
	void callForDelete(Tupple<K, V> tupple);
}
