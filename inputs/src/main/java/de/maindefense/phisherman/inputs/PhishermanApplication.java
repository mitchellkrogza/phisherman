package de.maindefense.phisherman.inputs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PhishermanApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhishermanApplication.class, args);
	}

}
