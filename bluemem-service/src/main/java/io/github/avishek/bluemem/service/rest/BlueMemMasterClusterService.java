package io.github.avishek.bluemem.service.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.avishek.bluemem.specification.BlueMemSpecification;

@RestController
public class BlueMemMasterClusterService {

	private final String URL = "/master-cluster";
	
	@Autowired
	private BlueMemSpecification<String, String> blueMemSpecification;
	
	@GetMapping(URL+"/timestamp")
	public void getTimestamp(HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.getWriter().write(String.valueOf(blueMemSpecification.getTimeStamp()));
	}
	
	@GetMapping(URL+"/ping")
	public void pong(@RequestParam("name") String name, HttpServletResponse httpServletResponse) throws IOException {
		System.out.println("######################## Master Clusters Connection ########################");
		System.out.println(name + " is connected");
		System.out.println("############################################################################");
		httpServletResponse.getWriter().write("pong");
	}
}
