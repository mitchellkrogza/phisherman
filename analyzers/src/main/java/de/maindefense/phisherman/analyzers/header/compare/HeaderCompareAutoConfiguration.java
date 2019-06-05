package de.maindefense.phisherman.analyzers.header.compare;

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
@ConditionalOnProperty(name = "analyzer.header.compare[0].operator")
@AutoConfigureBefore(value = {AnalyzerChainAutoConfiguration.class})
public class HeaderCompareAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderCompareAutoConfiguration.class);

  @Autowired
  private AnnotationConfigApplicationContext ctx;

  @Bean
  @ConfigurationProperties(prefix = "analyzer.header")
  HeaderCompareAnalyzerProperties getHeaderCompareAnalyzerProperties() {
    return new HeaderCompareAnalyzerProperties();
  }

  @PostConstruct
  void init() {
    HeaderCompareAnalyzerProperties p = getHeaderCompareAnalyzerProperties();
    if (CollectionUtils.isEmpty(p.getCompare())) {
      LOG.info("Total number of header compare analyzers to be initialized: 0");
    } else {
      LOG.info(
          "Start initializing header compare analyzers .. Total number of header compare analyzers to be initialized: "
              + p.getCompare().size());
      p.getCompare().forEach(s -> {
        LOG.info("Initializing header compare analyzers: " + s.toString());
        ctx.registerBean(UUID.randomUUID().toString(), HeaderCompareAnalyzer.class, s);
      });
    }
  }
}
