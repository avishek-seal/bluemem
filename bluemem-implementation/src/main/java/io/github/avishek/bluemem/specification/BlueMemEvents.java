package io.github.avishek.bluemem.specification;

import java.util.LinkedList;
import java.util.List;

import io.github.avishek.bluemem.core.BlueMemEventListener;
import io.github.avishek.bluemem.core.Tupple;

public interface BlueMemEvents<K, V> {

	List<BlueMemEventListener<Tupple<?, ?>>> BLUEMEMEVENTLISTENERS_PUT = new LinkedList<>();
	
	List<BlueMemEventListener<Tupple<?, ?>>> BLUEMEMEVENTLISTENERS_DELETE = new LinkedList<>();
	
	public void onPut(Tupple<K, V> tupple);
	
	public void onDelete(Tupple<K, V> tupple);
	
	default void addPutEventListener(BlueMemEventListener<Tupple<?, ?>> blueMemEventListener) {
		BLUEMEMEVENTLISTENERS_PUT.add(blueMemEventListener);
	}
	
	default void addDeleteEventListener(BlueMemEventListener<Tupple<?, ?>> blueMemEventListener) {
		BLUEMEMEVENTLISTENERS_DELETE.add(blueMemEventListener);
	}
}
