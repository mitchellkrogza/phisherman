package de.maindefense.phisherman.common;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

class AnalyzingProgressModelTest {

  @Test
  void testSerializeOriginalMessage() throws AddressException, MessagingException, IOException {
    AnalyzingProgressModel model = new AnalyzingProgressModel();
    MimeMessage message = new MimeMessage(dummySession());
    message.setFrom(new InternetAddress("from@bar.baz"));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress("to@bar.baz"));
    message.setSubject("This is the Subject Line");
    message.setText("This is actual message");
    model.setOriginalMessage(message);
    model.serializeOriginalMessage();
    assertNull(model.getOriginalMessage());
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    message.writeTo(os);
    byte[] expected = os.toByteArray();
    assertArrayEquals(expected, model.getOriginalMessageSerialized());
  }

  @Test
  void testDeSerializeOriginalMessage() throws AddressException, MessagingException, IOException {
    AnalyzingProgressModel model = new AnalyzingProgressModel();
    byte[] serializedMessage = new byte[] {68, 97, 116, 101, 58, 32, 84, 104, 117, 44, 32, 54, 32,
        74, 117, 110, 32, 50, 48, 49, 57, 32, 49, 51, 58, 51, 51, 58, 48, 54, 32, 43, 48, 50, 48,
        48, 32, 40, 67, 69, 83, 84, 41, 13, 10, 70, 114, 111, 109, 58, 32, 102, 114, 111, 109, 64,
        98, 97, 114, 46, 98, 97, 122, 13, 10, 84, 111, 58, 32, 116, 111, 64, 98, 97, 114, 46, 98,
        97, 122, 13, 10, 77, 101, 115, 115, 97, 103, 101, 45, 73, 68, 58, 32, 60, 49, 54, 54, 53,
        49, 57, 55, 53, 53, 50, 46, 48, 46, 49, 53, 53, 57, 56, 50, 48, 55, 56, 54, 51, 50, 54, 64,
        100, 62, 13, 10, 83, 117, 98, 106, 101, 99, 116, 58, 32, 84, 104, 105, 115, 32, 105, 115,
        32, 116, 104, 101, 32, 83, 117, 98, 106, 101, 99, 116, 32, 76, 105, 110, 101, 13, 10, 77,
        73, 77, 69, 45, 86, 101, 114, 115, 105, 111, 110, 58, 32, 49, 46, 48, 13, 10, 67, 111, 110,
        116, 101, 110, 116, 45, 84, 121, 112, 101, 58, 32, 116, 101, 120, 116, 47, 112, 108, 97,
        105, 110, 59, 32, 99, 104, 97, 114, 115, 101, 116, 61, 117, 115, 45, 97, 115, 99, 105, 105,
        13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 84, 114, 97, 110, 115, 102, 101, 114, 45, 69,
        110, 99, 111, 100, 105, 110, 103, 58, 32, 55, 98, 105, 116, 13, 10, 13, 10, 84, 104, 105,
        115, 32, 105, 115, 32, 97, 99, 116, 117, 97, 108, 32, 109, 101, 115, 115, 97, 103, 101};
    model.setOriginalMessageSerialized(serializedMessage);
    model.deSerializeOriginalMessage();
    assertNull(model.getOriginalMessageSerialized());

    Message originalMessage = model.getOriginalMessage();

    assertEquals(new InternetAddress("from@bar.baz"), originalMessage.getFrom()[0]);
    assertEquals(new InternetAddress("to@bar.baz"),
        originalMessage.getRecipients(Message.RecipientType.TO)[0]);
    assertNull(originalMessage.getRecipients(Message.RecipientType.CC));
    assertNull(originalMessage.getRecipients(Message.RecipientType.BCC));
    assertEquals("This is the Subject Line", originalMessage.getSubject());
    assertEquals("This is actual message", originalMessage.getContent());
  }

  private Session dummySession() {
    return Session.getDefaultInstance(new Properties());
  }

}
