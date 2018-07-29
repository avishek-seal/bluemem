package io.github.avishek.bluemem.core;

import java.io.Serializable;
import java.util.Objects;

public class Tupple<K, V> implements Serializable{

	private static final long serialVersionUID = -6314669228663465306L;
	
	private K key;
	
	private Value<V> value;
	
	private Integer duration;
	
	private String sender;

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public Value<V> getValue() {
		return value;
	}

	public void setValue(Value<V> value) {
		this.value = value;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getPayload() {
		String payload = "#key|#value|#duration|#timestamp|#sender"
				.replace("#key", key.toString())
				.replace("#value", Objects.isNull(value) ? "" : Objects.isNull(value.getValue()) ? "" :value.getValue().toString())
				.replace("#duration", Objects.isNull(duration) ? "0" : duration.toString())
				.replace("#timestamp", Objects.isNull(value) ? "" : String.valueOf(value.getTimestamp()))
				.replace("#sender", Objects.isNull(sender) ? "" : sender);
		
		return payload;
	}

	@Override
	public String toString() {
		return "Tupple [key=" + key + ", value=" + value + ", duration=" + duration + "]";
	}
}
