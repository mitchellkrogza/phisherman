package de.maindefense.phisherman.analyzers.header.regex;

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
import org.springframework.util.CollectionUtils;

@Configuration
@ConditionalOnProperty(name = "analyzer.header.regex[0].pattern")
@AutoConfigureBefore(value = {AnalyzerChainAutoConfiguration.class})
public class HeaderMatchesRegexAutoConfiguration {

  private static final Logger LOG =
      LoggerFactory.getLogger(HeaderMatchesRegexAutoConfiguration.class);

  @Autowired
  private AnnotationConfigApplicationContext ctx;

  @Bean
  @ConfigurationProperties(prefix = "analyzer.header")
  HeaderMatchesRegexAnalyzerProperties getHeaderMatchesRegexAnalyzerProperties() {
    return new HeaderMatchesRegexAnalyzerProperties();
  }

  @PostConstruct
  void init() {
    HeaderMatchesRegexAnalyzerProperties p = getHeaderMatchesRegexAnalyzerProperties();
    if (CollectionUtils.isEmpty(p.getRegex())) {
      LOG.info("Total number of header regex analyzers to be initialized: 0");
    } else {
      LOG.info(
          "Start initializing header regex analyzers .. Total number of header regex analyzers to be initialized: "
              + p.getRegex().size());
      p.getRegex().forEach(s -> {
        LOG.info("Initializing header regex analyzers: " + s.toString());
        ctx.registerBean(UUID.randomUUID().toString(), HeaderMatchesRegexAnalyzer.class, s);
      });
    }
  }
}
