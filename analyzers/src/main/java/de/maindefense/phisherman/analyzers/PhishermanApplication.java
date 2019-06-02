package de.maindefense.phisherman.analyzers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"de.maindefense.phisherman"})
@EnableConfigurationProperties
public class PhishermanApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhishermanApplication.class, args);
	}

}
