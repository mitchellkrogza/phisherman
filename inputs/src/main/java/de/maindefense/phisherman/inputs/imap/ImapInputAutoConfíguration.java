package de.maindefense.phisherman.inputs.imap;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.imap.ImapInputProperties.ImapServerProperties;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;


@Configuration
@ConditionalOnProperty(name = "input.imap.servers[0].hostname")
@EnableScheduling
public class ImapInputAutoConfíguration {

  private static final Logger LOG = LoggerFactory.getLogger(ImapInputAutoConfíguration.class);

  private final List<ImapMailInput> imapMailInputs = new ArrayList<>();

  @Autowired
  private FileSystemDataProvider fileSystemDataProvider;

  @Bean
  @ConfigurationProperties(prefix = "input.imap")
  ImapInputProperties getImapInputProperties() {
    return new ImapInputProperties();
  }

  @Scheduled(fixedDelay = 60000)
  void fetchMailsFromImapInputs() {
    imapMailInputs.forEach(i -> {
      i.fetchInput();
    });
  }

  @PostConstruct
  void init() {
    ImapInputProperties imapInputProperties = getImapInputProperties();
    if (CollectionUtils.isEmpty(imapInputProperties.getServers())) {
      LOG.info("Total number of imap inputs to be initialized: 0");
    } else {
      LOG.info("Start initializing imap inputs... Total number of imap inputs to be initialized: "
          + imapInputProperties.getServers().size());
      imapInputProperties.getServers().forEach(s -> {
        LOG.info("Initializing imap input: " + s.toString());
        initializeImapInput(s);
      });
    }
  }

  void initializeImapInput(ImapServerProperties imapProperties) {
    imapMailInputs.add(new ImapMailInput(imapProperties, fileSystemDataProvider));
  }
}
