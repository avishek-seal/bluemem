package io.github.avishek.bluemem.implementation;

import java.util.HashMap;
import java.util.Objects;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.core.DataStore;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.exception.NoDataToPutException;
import io.github.avishek.bluemem.specification.BlueMemEvents;
import io.github.avishek.bluemem.specification.BlueMemScheduler;
import io.github.avishek.bluemem.specification.BlueMemSpecification;

@Component
public class BlueMemImplementation implements BlueMemSpecification<String, String>, InitializingBean, DisposableBean{

	private DataStore<String, String> DATASTORE;
	
	@Autowired
	private BlueMemEvents<String, String> blueMemEvents;
	
	@Autowired
	private BlueMemScheduler blueMemScheduler;
	
	private int initialCapacity = 100;
	
	private float loadFactor = 0.9f;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		DATASTORE = new DataStore<>(new HashMap<>(initialCapacity, loadFactor));
	}
	
	@Override
	public void destroy() throws Exception {
		
	}
	
	@Override
	public String get(String key) {
		return DATASTORE.get(key);
	}
	
	@Override
	public void put(Tupple<String, String> tupple) {
		if(Objects.isNull(tupple)) {
			throw new NoDataToPutException("No data to put");
		} else if(Objects.isNull(tupple.getKey())){
			throw new NoDataToPutException("No key to put");
		} else {
			if(Objects.isNull(tupple.getDuration())) {
				DATASTORE.put(tupple.getKey(), tupple.getValue());
			} else {
				DATASTORE.put(tupple.getKey(), tupple.getValue(), tupple.getDuration());
				
				blueMemScheduler.schedule(tupple.getDuration(), () -> {
					DATASTORE.delete(tupple.getKey());
				});
			}
			
			blueMemEvents.onPut(tupple);
		}
	}
	
	@Override
	public void delete(String key) {
		DATASTORE.delete(key);
	}
	
	@Override
	public String keys() {
		return DATASTORE.getAllKeys();
	}
}
