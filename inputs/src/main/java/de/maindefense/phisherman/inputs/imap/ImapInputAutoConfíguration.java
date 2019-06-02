package de.maindefense.phisherman.inputs.imap;

import de.maindefense.phisherman.inputs.imap.ImapInputProperties.ImapProperties;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@ConditionalOnProperty(name = "input.imap")
@EnableScheduling
public class ImapInputAutoConfíguration {

  private static final Logger LOG = LoggerFactory.getLogger(ImapInputAutoConfíguration.class);

  @Autowired
  private ImapInputProperties imapInputProperties;

  @PostConstruct
  void init() {
    LOG.debug("Start initializing imap inputs... Total number of imap inputs to be initialized: "
        + imapInputProperties.getServers().size());
    imapInputProperties.getServers().forEach(s -> {
      LOG.debug("Initializing imap input: " + s.toString());

      

    });
  }
  
  void initializeImapInput(ImapProperties imapProperties) {
    
  }
}
