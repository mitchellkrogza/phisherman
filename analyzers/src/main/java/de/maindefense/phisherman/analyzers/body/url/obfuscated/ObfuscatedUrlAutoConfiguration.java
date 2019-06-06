package de.maindefense.phisherman.analyzers.body.url.obfuscated;

import de.maindefense.phisherman.analyzers.config.AnalyzerChainAutoConfiguration;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "analyzer.body.url.obfuscated.enabled")
@AutoConfigureBefore(value = {AnalyzerChainAutoConfiguration.class})
public class ObfuscatedUrlAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(ObfuscatedUrlAutoConfiguration.class);

  @Autowired
  private AnnotationConfigApplicationContext ctx;

  @Bean
  @ConfigurationProperties(prefix = "analyzer.body.url.obfuscated")
  ObfuscatedUrlAnalyzerProperties getObfuscatedUrlAnalyzerProperties() {
    return new ObfuscatedUrlAnalyzerProperties();
  }

  @PostConstruct
  void init() {
    ObfuscatedUrlAnalyzerProperties p = getObfuscatedUrlAnalyzerProperties();
    if (p.isEnabled()) {
    } else {
      LOG.info("Initializing obfuscated url analyzers");
      ctx.registerBean(UUID.randomUUID().toString(), ObfuscatedUrlAnalyzer.class);
    }
  }
}
