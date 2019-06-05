package de.maindefense.phisherman.common;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnMissingBean(value = {QueueProvider.class})
public class AnalyzingQueueProviderAutoConfiguration {

  @Autowired
  private Environment env;

  @Bean
  QueueProvider analyzingQueueProvider() throws IOException {
    return new QueueProvider(env);
  }

}
