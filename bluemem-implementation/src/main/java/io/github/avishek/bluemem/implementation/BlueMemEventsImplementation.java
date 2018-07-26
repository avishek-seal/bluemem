package io.github.avishek.bluemem.implementation;

import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.specification.BlueMemEvents;

@Component
public class BlueMemEventsImplementation implements BlueMemEvents<String, String>{

	@Override
	public void onPut(Tupple<String, String> tupple) {
		System.out.println("PUT :: " + tupple);
		
		BLUEMEMEVENTLISTENERS_PUT.forEach((listener) -> {
			listener.execute(tupple);
		});
	}

	@Override
	public void onDelete(Tupple<String, String> tupple) {
		System.out.println("DELETE :: " + tupple);
		
		BLUEMEMEVENTLISTENERS_DELETE.forEach((listener) -> {
			listener.execute(tupple);
		});
	}
}
