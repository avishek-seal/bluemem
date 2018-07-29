package io.github.avishek.bluemem.implementation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.configuration.maker.BluememConfiguration;
import io.github.avishek.bluemem.core.DataStore;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.core.Value;
import io.github.avishek.bluemem.exception.NoDataToPutException;
import io.github.avishek.bluemem.specification.BlueMemEvents;
import io.github.avishek.bluemem.specification.BlueMemMasterClusterSpecification;
import io.github.avishek.bluemem.specification.BlueMemScheduler;
import io.github.avishek.bluemem.specification.BlueMemSpecification;

@Component
public class BlueMemImplementation implements BlueMemSpecification<String, String>, InitializingBean {

	private DataStore<String, Value<String>> DATASTORE;

	private static final Object LOCK = new Object();

	@Autowired
	private BluememConfiguration bluememConfiguration;

	@Autowired
	private BlueMemEvents<String, String> blueMemEvents;

	@Autowired
	private BlueMemScheduler blueMemScheduler;
	
	@Autowired
	private BlueMemMasterClusterSpecification<String, String> blueMemMasterClusterSpecification;

	private int initialCapacity = 100;

	private float loadFactor = 0.9f;

	@Override
	public long getTimeStamp() throws MalformedURLException, IOException {
		if(bluememConfiguration.isRoot()) {
			synchronized (LOCK) {
				return System.currentTimeMillis();
			}
		} else {
			return blueMemMasterClusterSpecification.getTimestamp();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		blueMemEvents.addPutEventListener((tupple) -> {
			blueMemMasterClusterSpecification.callForPut(tupple);
		});
		
		blueMemEvents.addDeleteEventListener((tupple) -> {
			blueMemMasterClusterSpecification.callForDelete(tupple);
		});
		
		try (FileInputStream is = new FileInputStream(bluememConfiguration.getBluememDataFileURL());
				ObjectInputStream ois = new ObjectInputStream(is)) {
			DATASTORE = (DataStore<String, Value<String>>) ois.readObject();
			decideAndRemove();
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("No Data Persisted");
		}

		if (Objects.isNull(DATASTORE)) {
			DATASTORE = new DataStore<>(new ConcurrentHashMap<>(initialCapacity, loadFactor), new ConcurrentHashMap<>(initialCapacity, loadFactor));
		}
		
		new Thread(() -> {
			blueMemMasterClusterSpecification.traceNodes();
		}).start();
	}

	private void decideAndRemove() throws MalformedURLException, IOException {
		final long currentTimestamp = getTimeStamp();
		
		for(Map.Entry<String, Value<String>> entry : DATASTORE.getSTORE().entrySet()) {
			int remaining = isDeletable(entry.getValue().getTimestamp(), currentTimestamp, DATASTORE.getDURATION().get(entry.getKey()));
			if(remaining <= 0) {
				delete(entry.getKey(), entry.getValue(), null, false);
			} else {
				blueMemScheduler.schedule(remaining, () -> {
					delete(entry.getKey(), entry.getValue(), null, false);
				});
			}
		}
	}
	
	private int isDeletable(long entryTimestamp, long currentTimestamp, int duration) {
		long timespend = (currentTimestamp - entryTimestamp) / 1000;
		
		return duration - (int)timespend; 
	}
	
	@PreDestroy
	public void destroy() throws Exception {
		final FileOutputStream dataFileOutputStream = new FileOutputStream(bluememConfiguration.getBluememDataFileURL());
		final ObjectOutputStream dataObjectOutputStream = new ObjectOutputStream(dataFileOutputStream);
		dataObjectOutputStream.writeObject(DATASTORE);
		IOUtils.closeQuietly(dataObjectOutputStream);
		IOUtils.closeQuietly(dataFileOutputStream);
	}

	@Override
	public String get(String key) {
		return DATASTORE.get(key) == null ? null : DATASTORE.get(key).getValue();
	}

	@Override
	public void put(Tupple<String, String> tupple) {
		if (Objects.isNull(tupple)) {
			throw new NoDataToPutException("No data to put");
		} else if (Objects.isNull(tupple.getKey())) {
			throw new NoDataToPutException("No key to put");
		} else {
			if (Objects.isNull(tupple.getDuration())) {
				decideAndPut(tupple, false);
			} else {
				decideAndPut(tupple, true);

				blueMemScheduler.schedule(tupple.getDuration(), () -> {
					delete(tupple, true);
				});
			}
		}
	}

	private void decideAndPut(Tupple<String, String> tupple, boolean duration) {
		final Value<String> value = DATASTORE.get(tupple.getKey());

		if (Objects.isNull(value)) {
			if (duration) {
				DATASTORE.put(tupple.getKey(), tupple.getValue(), tupple.getDuration());
			} else {
				DATASTORE.put(tupple.getKey(), tupple.getValue());
			}

			blueMemEvents.onPut(tupple);
		} else if (value.getTimestamp() < tupple.getValue().getTimestamp()) {// only latest key<->value will be stored
			DATASTORE.put(tupple.getKey(), tupple.getValue());

			if (duration) {
				DATASTORE.put(tupple.getKey(), tupple.getValue(), tupple.getDuration());
			} else {
				DATASTORE.put(tupple.getKey(), tupple.getValue());
			}

			blueMemEvents.onPut(tupple);
		}
	}

	@Override
	public void delete(Tupple<String, String> tupple, boolean scheduler) {
		delete(tupple.getKey(), tupple.getValue(), tupple, scheduler);
	}
	
	private void delete(String key, Value<String> value, Tupple<String, String> tupple, boolean scheduler) {
		if(StringUtils.isNotBlank(key) && Objects.nonNull(DATASTORE.get(key)) && DATASTORE.get(key).getTimestamp() <= value.getTimestamp()) {//delete only for latest call
			DATASTORE.delete(key);
			
			if(!scheduler) {
				if(Objects.isNull(tupple)) {
					Tupple<String, String> tupple2 = new Tupple<String, String>();
					blueMemEvents.onDelete(tupple2);
				} else {
					blueMemEvents.onDelete(tupple);
				}
			}
		}
	}

	@Override
	public String keys() {
		return DATASTORE.getAllKeys();
	}
}
