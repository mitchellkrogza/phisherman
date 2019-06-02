package de.maindefense.phisherman.analyzers.config;

import de.maindefense.phisherman.analyzers.Analyzer;
import de.maindefense.phisherman.analyzers.AttachedMessageProvider;
import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.Input;
import de.maindefense.phisherman.inputs.exception.InputException;
import de.maindefense.phisherman.inputs.fs.LocalFileSystemMailInput;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty(name = "analyzer")
@EnableScheduling
public class AnalyzerChainAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerChainAutoConfiguration.class);

  private final List<Analyzer> analyzerChain = new ArrayList<>();

  @Autowired
  private AttachedMessageProvider attachedMessageProvider;

  @Autowired
  private FileSystemDataProvider fileSystemDataProvider;


  // @Bean
  // @ConfigurationProperties(prefix = "input.imap")
  // ImapInputProperties getImapInputProperties() {
  // return new ImapInputProperties();
  // }

  @PostConstruct
  void init() {
    // TODO: load all analyzers

    // sort analyzes by ordinal. can be configured in property file
    analyzerChain.sort((a1, a2) -> {
      if (a1.getOrdinal() < a2.getOrdinal()) {
        return -1;
      } else if (a1.getOrdinal() > a2.getOrdinal()) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  @Scheduled(initialDelay = 10000, fixedDelay = 10000)
  void analyzeMessages() {
    List<Message> messagesToByAnalyzed = getMessagesToByAnalyzed();
    messagesToByAnalyzed.forEach(message -> {
      AtomicLong result = new AtomicLong(0);
      try {
        List<Message> attachedMessages = attachedMessageProvider.getAttachedMessages(message);
        attachedMessages.forEach(attachedMessage -> {
          analyzerChain.forEach(analyzer -> {
            result.addAndGet(analyzer.analyze(attachedMessage));
          });
        });
        // TODO: safe result, delete/archive input
      } catch (IOException | MessagingException e) {
        LOG.error("Error analyzing the message", e);
      }
      // TODO: proceed with re result
      try {
        LOG.info("Analysis result for " + message.getSubject() + ": [" + result.toString() + "]");
      } catch (MessagingException e) {
        LOG.error("Error getting subject of the message", e);
      }
    });
    LOG.info(messagesToByAnalyzed.toString());
  }

  List<Message> getMessagesToByAnalyzed() {
    LocalFileSystemMailInput input = new LocalFileSystemMailInput(
        Paths.get(fileSystemDataProvider.getDataDir().toString(), Input.INPUT_DIRECTORY_NAME),
        fileSystemDataProvider);
    List<Message> messagesToBeAnalyzed = new ArrayList<>();
    input.walkRecursiveStartingFrom(p -> {
      try {
        Message msg = input.getMessageFromLocalFileSystemPath(p);
        messagesToBeAnalyzed.add(msg);
      } catch (InputException e) {
        LOG.error(
            "Message could not loaded to be analyzed. Will be retried on next analyze attempt.", e);
      }
    });
    return messagesToBeAnalyzed;
  }
}
