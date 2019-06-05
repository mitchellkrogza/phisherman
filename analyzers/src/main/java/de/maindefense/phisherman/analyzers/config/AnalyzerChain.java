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
import java.util.stream.Collectors;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnalyzerChain {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerChain.class);

  private AttachedMessageProvider attachedMessageProvider;
  private FileSystemDataProvider fileSystemDataProvider;

  private final List<Analyzer> analyzers = new ArrayList<>();


  public AnalyzerChain(AttachedMessageProvider attachedMessageProvider,
      FileSystemDataProvider fileSystemDataProvider) {
    super();
    this.attachedMessageProvider = attachedMessageProvider;
    this.fileSystemDataProvider = fileSystemDataProvider;
  }

  public void registerAnalyzer(Analyzer analyzer) {
    analyzers.add(analyzer);
  }

  public void analyzeAllMessages() {
    getMessagesToByAnalyzed().forEach(message -> {
      // analyze each message
      AtomicLong result = new AtomicLong(0);
      analyzeAttachedMessagesRecursively(message, result);
      // TODO: proceed with the result
      try {
        LOG.info("Analysis result for " + message.getSubject() + ": [" + result.toString() + "]");
      } catch (MessagingException e) {
        LOG.error("Error getting subject of the message", e);
      }
    });
  }

  protected void analyzeAttachedMessagesRecursively(Message originalMessage, AtomicLong result) {
    try {
      List<Message> attachedMessages = attachedMessageProvider.getAttachedMessages(originalMessage);
      attachedMessages.forEach(attachedMessage -> {
        // if there is an attachedMessage, check it has recursively attached messages again
        analyzeAttachedMessagesRecursively(attachedMessage, result);
        // check attached message with all registered analyzers
        getAnalyzersSortedByOrder().forEach(analyzer -> {
          result.addAndGet(analyzer.analyze(attachedMessage));
        });
      });
    } catch (IOException | MessagingException e) {
      LOG.error("Error analyzing the message", e);
    }
  }

  public List<Analyzer> getAnalyzersSortedByOrder() {
    return analyzers.stream().sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
        .collect(Collectors.toList());
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
