package io.github.avishek.bluemem.specification;

import java.io.IOException;
import java.net.MalformedURLException;

import io.github.avishek.bluemem.core.Tupple;

public interface BlueMemConverter {

	int KEY_INDEX = 0;
	int VALUE_INDEX = 1;
	int DURATION_INDEX = 2;
	int TIMESTAMP_INDEX = 3;
	int SENDER_INDEX = 4;
	
	String SPLITTER = "\\|";
	
	String PAYLOAD_TEMPLATE = "#key|#value|#duration|#timestamp|#sender";
	
	String KEY = "#key";
	String VALUE = "#value";
	String DURATION = "#duration";
	String TIMESTAMP="#timestamp";
	String SENDER = "#sender";
	
	String toPayload(Tupple<String, String> tupple);
	
	Tupple<String, String> toModel(String body) throws MalformedURLException, IOException;
}
