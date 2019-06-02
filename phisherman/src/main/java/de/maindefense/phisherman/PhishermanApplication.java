package de.maindefense.phisherman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
public class PhishermanApplication {

  public static void main(String[] args) {
    SpringApplication.run(PhishermanApplication.class, args);
  }

}
