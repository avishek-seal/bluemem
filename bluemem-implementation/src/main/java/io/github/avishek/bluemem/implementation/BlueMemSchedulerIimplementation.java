package io.github.avishek.bluemem.implementation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.core.BlueMemTask;
import io.github.avishek.bluemem.specification.BlueMemScheduler;

@Component
public class BlueMemSchedulerIimplementation implements BlueMemScheduler{

	@Autowired
	private TaskScheduler executor;
	
	@Override
	public void schedule(int duration, BlueMemTask blueMemTask) {
		System.out.println("Scheduled task to execute after " + duration + " Task :: " + blueMemTask);
		executor.schedule(blueMemTask, Date.from(LocalDateTime.now().plusSeconds(duration).atZone(ZoneId.systemDefault()).toInstant()));
	}
}
