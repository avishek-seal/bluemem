package io.github.avishek.bluemem.implementation;

import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.core.BlueMemTask;
import io.github.avishek.bluemem.specification.BlueMemScheduler;

@Component
public class BlueMemSchedulerIimplementation implements BlueMemScheduler{

	@Override
	public void schedule(int duration, BlueMemTask blueMemTask) {
		System.out.println("Scheduled task to execute after " + duration + " Task :: " + blueMemTask);
	}
}
