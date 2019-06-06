package de.maindefense.phisherman.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class AnalyzingProgressModel {

  private String sourceInput;
  private Message originalMessage;
  private byte[] originalMessageSerialized;
  private final Map<String, Long> analyzerResults = new HashMap<>();
  private final Map<String, String> outputResults = new HashMap<>();

  public AnalyzingProgressModel() {
    this(null, null);
  }

  public AnalyzingProgressModel(String sourceInput, Message originalMessage) {
    this.sourceInput = sourceInput;
    this.originalMessage = originalMessage;
  }

  public void setSourceInput(String sourceInput) {
    this.sourceInput = sourceInput;
  }

  public String getSourceInput() {
    return sourceInput;
  }

  public void setOriginalMessage(Message originalMessage) {
    this.originalMessage = originalMessage;
  }

  public Message getOriginalMessage() {
    return originalMessage;
  }

  public Map<String, Long> getAnalyzerResults() {
    return analyzerResults;
  }

  public Map<String, String> getOutputResults() {
    return outputResults;
  }

  public void setOriginalMessageSerialized(byte[] originalMessageSerialized) {
    this.originalMessageSerialized = originalMessageSerialized;
  }

  public byte[] getOriginalMessageSerialized() {
    return originalMessageSerialized;
  }

  public void serializeOriginalMessage() throws MessagingException, IOException {
    if (originalMessage == null) {
      return;
    }

    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      originalMessage.writeTo(os);
      originalMessageSerialized = os.toByteArray();
      originalMessage = null;
    }
  }

  public void deSerializeOriginalMessage() throws MessagingException, IOException {
    if (originalMessageSerialized == null) {
      return;
    }

    try (ByteArrayInputStream is = new ByteArrayInputStream(originalMessageSerialized)) {
      originalMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), is);
      originalMessageSerialized = null;
    }
  }
}
