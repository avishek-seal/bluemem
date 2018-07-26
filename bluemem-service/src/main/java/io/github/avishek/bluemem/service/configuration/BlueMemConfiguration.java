package io.github.avishek.bluemem.service.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "io.github.avishek.bluemem" })
@EnableAutoConfiguration
public class BlueMemConfiguration {

}
