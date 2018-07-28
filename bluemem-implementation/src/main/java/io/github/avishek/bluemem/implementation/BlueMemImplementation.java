package io.github.avishek.bluemem.implementation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.configuration.maker.BluememConfiguration;
import io.github.avishek.bluemem.core.DataStore;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.core.Value;
import io.github.avishek.bluemem.exception.NoDataToPutException;
import io.github.avishek.bluemem.specification.BlueMemEvents;
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

	private int initialCapacity = 100;

	private float loadFactor = 0.9f;

	@Override
	public long getTimeStamp() {
		synchronized (LOCK) {
			return System.currentTimeMillis();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		try (FileInputStream is = new FileInputStream(bluememConfiguration.getBluememDataFileURL());
				ObjectInputStream ois = new ObjectInputStream(is)) {
			DATASTORE = (DataStore<String, Value<String>>) ois.readObject();
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("No Data Persisted");
		}

		if (Objects.isNull(DATASTORE)) {
			DATASTORE = new DataStore<>(new ConcurrentHashMap<>(initialCapacity, loadFactor), new ConcurrentHashMap<>(initialCapacity, loadFactor));
		}
	}

	@PreDestroy
	public void destroy() throws Exception {
		final FileOutputStream fos = new FileOutputStream(bluememConfiguration.getBluememDataFileURL());
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(DATASTORE);
		IOUtils.closeQuietly(oos);
		IOUtils.closeQuietly(fos);
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
					DATASTORE.delete(tupple.getKey());
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
	public void delete(String key) {
		DATASTORE.delete(key);
	}

	@Override
	public String keys() {
		return DATASTORE.getAllKeys();
	}
}
