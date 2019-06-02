package de.maindefense.phisherman;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
public class PhishermanApplication {

  @Autowired
  private Environment env;
  
  @Bean
  FileSystemDataProvider getFileSystemDataProvider() {
    return new FileSystemDataProvider(env);
  }
  
  public static void main(String[] args) {
    SpringApplication.run(PhishermanApplication.class, args);
  }

}
