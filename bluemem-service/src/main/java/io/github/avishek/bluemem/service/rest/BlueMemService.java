package io.github.avishek.bluemem.service.rest;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.avishek.bluemem.core.BlueMemLogicOperation;
import io.github.avishek.bluemem.core.Tupple;
import io.github.avishek.bluemem.core.Value;
import io.github.avishek.bluemem.exception.BlueMemException;
import io.github.avishek.bluemem.specification.BlueMemSpecification;

@RestController
public class BlueMemService {

	private final String URL = "/bluemem";
	
	@Autowired
	private BlueMemSpecification<String, String> blueMemSpecification;
	
	@PostMapping(URL)
	public void post(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException{
		commonExecution(httpServletRequest, httpServletResponse, (tupple) -> {
			blueMemSpecification.put(tupple);
			return "Success";
		});
	}


	@PutMapping(URL)
	public void put(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
		commonExecution(httpServletRequest, httpServletResponse, (tupple) -> {
			blueMemSpecification.put(tupple);
			return "Success";
		});
	}
	
	@GetMapping(URL+"/keys/{key}")
	public void get(@PathVariable("key") String key, HttpServletResponse httpServletResponse) throws IOException {
		commonExecution(null, httpServletResponse, (tupple) -> {
			return blueMemSpecification.get(key);
		});
	}
	
	@GetMapping(URL+"/keys")
	public void getKeys(HttpServletResponse httpServletResponse) throws IOException {
		commonExecution(null, httpServletResponse, (tupple) -> {
			return blueMemSpecification.keys();
		});
	}
	
	@DeleteMapping(URL+"/keys/{key}")
	public void delete(@PathVariable("key") String key, HttpServletResponse httpServletResponse) throws IOException {
		commonExecution(null, httpServletResponse, (tupple) -> {
			blueMemSpecification.delete(key);
			return "Success";
		});
	}
	
	private void commonExecution(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlueMemLogicOperation<Tupple<String, String>> blueMemLogicOperation) throws IOException {
		try {
			String response = blueMemLogicOperation.execute(prepareTupple(httpServletRequest));
			httpServletResponse.getWriter().append(response);
		} catch (BlueMemException e) {
			httpServletResponse.getWriter().append(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			httpServletResponse.getWriter().append("Server Error");
		}
	}
	
	private Tupple<String, String> prepareTupple(HttpServletRequest httpServletRequest) throws IOException {
		if(Objects.isNull(httpServletRequest)) {
			return null;
		}
		
		final String body = httpServletRequest.getReader().readLine();
		final String[] tuppleProperties = body.split("\\|");
		final Tupple<String, String> tupple = new Tupple<>();
		
		if(Objects.nonNull(tuppleProperties)) {
			if(tuppleProperties.length >= 1) {
				tupple.setKey(tuppleProperties[0]);
			}
			
			if(tuppleProperties.length >= 2) {
				final Value<String> value = new Value<>(); 
				value.setValue(tuppleProperties[1]);
				tupple.setValue(value);
			}

			if(tuppleProperties.length >= 3) {
				tupple.setDuration(Integer.parseInt(tuppleProperties[2]));
			}
			
			if(tuppleProperties.length >= 4) {
				tupple.getValue().setTimestamp(Long.parseLong(tuppleProperties[3]));
			} else {
				if(Objects.isNull(tupple.getValue())) {
					final Value<String> value = new Value<>(); 
					value.setTimestamp(blueMemSpecification.getTimeStamp());
					tupple.setValue(value);
				} else {
					tupple.getValue().setTimestamp(blueMemSpecification.getTimeStamp());
				}
			}
		}
		
		return tupple;
	}
}
