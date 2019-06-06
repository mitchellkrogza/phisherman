package de.maindefense.phisherman.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.ObjectQueue.Converter;
import com.squareup.tape2.QueueFile;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class QueueProvider {

  private static final Logger LOG = LoggerFactory.getLogger(QueueProvider.class);

  private final ObjectQueue<AnalyzingProgressModel> analyzerQueue;
  private final ObjectQueue<AnalyzingProgressModel> outputQueue;
  private final ObjectMapper mapper;

  public QueueProvider(Environment env, ObjectMapper mapper) throws IOException {
    this.mapper = mapper;
    Converter<AnalyzingProgressModel> converter = createConverter();
    analyzerQueue = ObjectQueue.create(buildQueueFile(env, "analyzer"), converter);
    outputQueue = ObjectQueue.create(buildQueueFile(env, "output"), converter);
  }

  protected Converter<AnalyzingProgressModel> createConverter() {
    return new Converter<AnalyzingProgressModel>() {

      @Override
      public void toStream(AnalyzingProgressModel value, OutputStream sink) throws IOException {
        try (BufferedOutputStream os = new BufferedOutputStream(sink)) {
          value.serializeOriginalMessage();
          mapper.writeValue(os, value);
        } catch (MessagingException e) {
          LOG.error("Error serializing AnalyzingProgressModel");
        }
      }

      @Override
      public AnalyzingProgressModel from(byte[] source) throws IOException {
        AnalyzingProgressModel analyzingProgressModel =
            mapper.readValue(source, AnalyzingProgressModel.class);
        try {
          analyzingProgressModel.deSerializeOriginalMessage();
        } catch (MessagingException e) {
          LOG.error("Error deserializing AnalyzingProgressModel");
        }
        return analyzingProgressModel;
      }
    };
  }

  protected QueueFile buildQueueFile(Environment env, String queueName) throws IOException {
    Path parentPath =
        Paths.get(env.getProperty(PropertyNames.PROPERTY_NAME_DATADIR, "data"), "queue");
    Files.createDirectories(parentPath);
    return new QueueFile.Builder(Paths.get(parentPath.toString(), queueName).toFile()).build();
  }

  public boolean isAnalyzerQueueEmpty() {
    return analyzerQueue.isEmpty();
  }

  public boolean isOutputQueueEmpty() {
    return outputQueue.isEmpty();
  }

  public AnalyzingProgressModel getAnalyzerHead() throws IOException {
    return analyzerQueue.peek();
  }

  public AnalyzingProgressModel getOutputHead() throws IOException {
    return outputQueue.peek();
  }

  public void addToAnalyzerQueue(AnalyzingProgressModel model) throws IOException {
    analyzerQueue.add(model);
  }

  public void addToOutputQueue(AnalyzingProgressModel model) throws IOException {
    outputQueue.add(model);
  }

  public void removeFromAnalyzerQueue() throws IOException {
    analyzerQueue.remove();
  }

  public void removeFromOutputQueue() throws IOException {
    outputQueue.remove();
  }
}
