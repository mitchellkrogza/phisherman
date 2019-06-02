package de.maindefense.phisherman.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnMissingBean(value = {FileSystemDataProvider.class})
public class FileSystemDataProviderAutoConfiguration {

  @Autowired
  private Environment env;

  @Bean
  FileSystemDataProvider fileSystemDataProvider() {
    return new FileSystemDataProvider(env);
  }

}
