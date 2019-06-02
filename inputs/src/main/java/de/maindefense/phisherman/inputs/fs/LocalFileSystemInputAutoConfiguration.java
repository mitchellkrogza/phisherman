package de.maindefense.phisherman.inputs.fs;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;


@Configuration
@ConditionalOnProperty(name = "input.fs.paths")
@EnableScheduling
public class LocalFileSystemInputAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemInputAutoConfiguration.class);

  private final List<LocalFileSystemMailInput> imapMailInputs = new ArrayList<>();

  @Autowired
  private Environment env;
  
  @Value("${input.fs.path}")
  @Bean
  @ConfigurationProperties(prefix = "input.fs")
  LocalFimeSystemInputProperties getInputProperties() {
    return new LocalFimeSystemInputProperties();
  }

  @Bean
  FileSystemDataProvider getFileSystemDataProvider() {
    return new FileSystemDataProvider(env);
  }

  @Scheduled(fixedDelay = 60000)
  void fetchMailsFromImapInputs() {
    imapMailInputs.forEach(i -> {
      i.fetchInput();
    });
  }

  @PostConstruct
  void init() {
    LocalFimeSystemInputProperties imapInputProperties = getInputProperties();
    if (CollectionUtils.isEmpty(imapInputProperties.getPaths())) {
      LOG.info("Total number of local file system inputs to be initialized: 0");
    } else {
      LOG.info("Start initializing local file system inputs... Total number of imap inputs to be initialized: "
          + imapInputProperties.getPaths().size());
      imapInputProperties.getPaths().forEach(s -> {
        LOG.info("Initializing local file system input: " + s.toString());
        initializeLocalFileSystemInput(Paths.get(s));
      });
    }
  }

  void initializeLocalFileSystemInput(Path imapProperties) {
    imapMailInputs.add(new LocalFileSystemMailInput(imapProperties, getFileSystemDataProvider()));
  }
}
