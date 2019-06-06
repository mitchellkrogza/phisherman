package de.maindefense.phisherman.analyzers.config;

import de.maindefense.phisherman.analyzers.Analyzer;
import de.maindefense.phisherman.analyzers.AttachedMessageProvider;
import de.maindefense.phisherman.common.AnalyzingProgressModel;
import de.maindefense.phisherman.common.QueueProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnalyzerChain {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerChain.class);

  private AttachedMessageProvider attachedMessageProvider;
  private QueueProvider queueProvider;

  private final List<Analyzer> analyzers = new ArrayList<>();


  public AnalyzerChain(AttachedMessageProvider attachedMessageProvider,
      QueueProvider queueProvider) {
    this.attachedMessageProvider = attachedMessageProvider;
    this.queueProvider = queueProvider;
  }

  public void registerAnalyzer(Analyzer analyzer) {
    analyzers.add(analyzer);
  }

  public void analyzeAllMessages() {
    while (!queueProvider.isAnalyzerQueueEmpty()) {
      try {
        AnalyzingProgressModel model = queueProvider.getAnalyzerHead();
        analyzeAttachedMessagesRecursively(model.getOriginalMessage(), model);
        //TODO: remove stuff from input queue
        queueProvider.removeFromAnalyzerQueue();
        queueProvider.addToOutputQueue(model);
      } catch (IOException e) {
        LOG.error("Error getting/persisting progress to queue", e);
      }
    }
  }

  protected void analyzeAttachedMessagesRecursively(Message originalMessage,
      AnalyzingProgressModel model) {
    try {
      List<MimeMessage> attachedMessages = attachedMessageProvider.getAttachedMessages(originalMessage);
      attachedMessages.forEach(attachedMessage -> {
        // if there is an attachedMessage, check it has recursively attached messages again
        analyzeAttachedMessagesRecursively(attachedMessage, model);
        // check attached message with all registered analyzers
        getAnalyzersSortedByOrder().forEach(analyzer -> {
          model.getAnalyzerResults().put(analyzer.getAnalyzerName(),
              analyzer.analyze(attachedMessage));
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



}
