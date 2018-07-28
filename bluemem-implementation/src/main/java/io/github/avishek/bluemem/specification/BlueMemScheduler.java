package io.github.avishek.bluemem.specification;

import io.github.avishek.bluemem.core.BlueMemTask;

public interface BlueMemScheduler {

	public void schedule(int duration, BlueMemTask blueMemTask);
	
}
