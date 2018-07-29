package io.github.avishek.bluemem.implementation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.configuration.maker.BluememConfiguration;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.specification.BlueMemEvents;

@Component
public class BlueMemEventsImplementation implements BlueMemEvents<String, String>{

	@Autowired
	private BluememConfiguration bluememConfiguration;
	
	@Override
	public void onPut(Tupple<String, String> tupple) {
		System.out.println("PUT :: " + tupple);
		
		if(StringUtils.isBlank(tupple.getSender())) {
			tupple.setSender(bluememConfiguration.getNodeName());
		}
		
		new Thread(() -> {
			BLUEMEMEVENTLISTENERS_PUT.forEach((listener) -> {
				listener.execute(tupple);
			});
		}).start();
	}

	@Override
	public void onDelete(Tupple<String, String> tupple) {
		System.out.println("DELETE :: " + tupple);
		
		if(StringUtils.isBlank(tupple.getSender())) {
			tupple.setSender(bluememConfiguration.getNodeName());
		}
		
		new Thread(() -> {
			BLUEMEMEVENTLISTENERS_DELETE.forEach((listener) -> {
				listener.execute(tupple);
			});
		}).start();
		
	}
}
