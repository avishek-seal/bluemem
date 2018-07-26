package io.github.avishek.bluemem.core;

@FunctionalInterface
public interface BlueMemEventListener<T> {

	void execute(T t);
}
