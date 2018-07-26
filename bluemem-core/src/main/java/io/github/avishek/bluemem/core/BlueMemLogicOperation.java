package io.github.avishek.bluemem.core;

@FunctionalInterface
public interface BlueMemLogicOperation<T> {

	String execute(T t);
}
