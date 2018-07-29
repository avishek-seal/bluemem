package io.github.avishek.bluemem.implementation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.core.Value;
import io.github.avishek.bluemem.specification.BlueMemConverter;
import io.github.avishek.bluemem.specification.BlueMemSpecification;

@Component
public class BlueMemConverterImplementation implements BlueMemConverter {

	@Autowired
	private BlueMemSpecification<String, String> blueMemSpecification;
	
	@Override
	public Tupple<String, String> toModel(String body) throws MalformedURLException, IOException {
		final String[] tuppleProperties = body.split(SPLITTER);
		final Tupple<String, String> tupple = new Tupple<>();
		
		if(Objects.nonNull(tuppleProperties)) {
			if(tuppleProperties.length >= 1) {
				tupple.setKey(tuppleProperties[KEY_INDEX]);
			}
			
			if(tuppleProperties.length >= 2) {
				final Value<String> value = new Value<>(); 
				value.setValue(tuppleProperties[VALUE_INDEX]);
				tupple.setValue(value);
			}

			if(tuppleProperties.length >= 3 && !StringUtils.isEmpty(tuppleProperties[2])) {
				tupple.setDuration(Integer.parseInt(tuppleProperties[DURATION_INDEX]));
			}
			
			if(tuppleProperties.length >= 4) {
				tupple.getValue().setTimestamp(Long.parseLong(tuppleProperties[TIMESTAMP_INDEX]));
			} else {
				if(Objects.isNull(tupple.getValue())) {
					final Value<String> value = new Value<>(); 
					value.setTimestamp(blueMemSpecification.getTimeStamp());
					tupple.setValue(value);
				} else {
					tupple.getValue().setTimestamp(blueMemSpecification.getTimeStamp());
				}
			}
			
			if(tuppleProperties.length >= 5 && !StringUtils.isEmpty(tuppleProperties[SENDER_INDEX])) {
				tupple.setSender(tuppleProperties[SENDER_INDEX]);
			}
		}
		
		return tupple;
	
	}
	
	@Override
	public String toPayload(Tupple<String, String> tupple) {
		return PAYLOAD_TEMPLATE
				.replace(KEY, tupple.getKey().toString())
				.replace(VALUE, Objects.isNull(tupple.getValue()) ? "" : Objects.isNull(tupple.getValue().getValue()) ? "" :tupple.getValue().getValue().toString())
				.replace(DURATION, Objects.isNull(tupple.getDuration()) ? "0" : tupple.getDuration().toString())
				.replace(TIMESTAMP, Objects.isNull(tupple.getValue()) ? "" : String.valueOf(tupple.getValue().getTimestamp()))
				.replace(SENDER, Objects.isNull(tupple.getSender()) ? "" : tupple.getSender());
	}
}
