package de.maindefense.phisherman.phisherman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
public class PhishermanApplication {

  public static void main(String[] args) {
    SpringApplication.run(PhishermanApplication.class, args);
  }

}
