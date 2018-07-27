package io.github.avishek.bluemem.communicate;

import java.io.IOException;
import java.net.MalformedURLException;

public interface BlueMemAgent {

	String get(String url) throws MalformedURLException, IOException;
	
	String post(String url, String payload) throws MalformedURLException, IOException;
	
	String delete(String url, String payload) throws MalformedURLException, IOException;
}
