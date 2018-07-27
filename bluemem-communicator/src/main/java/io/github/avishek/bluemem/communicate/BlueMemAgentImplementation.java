package io.github.avishek.bluemem.communicate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class BlueMemAgentImplementation implements BlueMemAgent{

	private String CONTENT_TYPE = "application/text";
	
	@Override
	public String get(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", CONTENT_TYPE);
		
		try{
			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}
			
			return IOUtils.toString(connection.getInputStream());
		  } finally {
			  connection.disconnect();
		  }
	}
	
	@Override
	public String post(String urlString, String payload) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		
		try (OutputStream os = connection.getOutputStream()){
			os.write(payload.getBytes());
			os.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}

			return IOUtils.toString(connection.getInputStream());
		  } finally {
			  connection.disconnect();
		  }
	}
	
	@Override
	public String delete(String urlString, String payload) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("DELETE");
		connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		
		try (OutputStream os = connection.getOutputStream()){
			os.write(payload.getBytes());
			os.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}

			return IOUtils.toString(connection.getInputStream());
		  } finally {
			  connection.disconnect();
		  }
	}
}
