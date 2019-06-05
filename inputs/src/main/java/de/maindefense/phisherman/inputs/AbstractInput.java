package de.maindefense.phisherman.inputs;

import de.maindefense.phisherman.common.AnalyzingProgressModel;
import de.maindefense.phisherman.common.QueueProvider;
import java.io.IOException;
import javax.mail.Message;

public abstract class AbstractInput implements Input {

  protected void addToQueue(Message message, QueueProvider queueProvider) throws IOException {
    queueProvider.addToAnalyzerQueue(new AnalyzingProgressModel(getInputName(), message));
  }

}
