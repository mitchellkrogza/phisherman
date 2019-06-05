package de.maindefense.phisherman.analyzers.config;

import de.maindefense.phisherman.analyzers.Analyzer;
import de.maindefense.phisherman.analyzers.AttachedMessageProvider;
import de.maindefense.phisherman.common.FileSystemDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnMissingBean(value = {AnalyzerChain.class})
@EnableScheduling
public class AnalyzerChainAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerChainAutoConfiguration.class);

  @Autowired
  private AttachedMessageProvider attachedMessageProvider;

  @Autowired
  private FileSystemDataProvider fileSystemDataProvider;

  @Autowired
  private ApplicationContext ctx;

  @Bean
  AnalyzerChain getAnalyzerChain() {
    AnalyzerChain analyzerChain =
        new AnalyzerChain(attachedMessageProvider, fileSystemDataProvider);
    registerAllAnalyzers(analyzerChain);
    return analyzerChain;
  }

  @Scheduled(initialDelay = 10000, fixedDelay = 10000)
  void analyzeMessages() {
    getAnalyzerChain().analyzeAllMessages();
  }

  private void registerAllAnalyzers(AnalyzerChain analyzerChain) {
    ctx.getBeansOfType(Analyzer.class).forEach((n, a) -> analyzerChain.registerAnalyzer(a));
  }
}
