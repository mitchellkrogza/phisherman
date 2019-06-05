package de.maindefense.phisherman.analyzers;

import com.sun.mail.imap.IMAPNestedMessage;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.simplejavamail.converter.EmailConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AttachedMessageProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AttachedMessageProvider.class);

  public List<Message> getAttachedMessages(Message message) throws IOException, MessagingException {
    Object content = message.getContent();
    List<Message> attachedMessages = new ArrayList<>();

    if (content instanceof Multipart) {
      Multipart multipart = (Multipart) content;
      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        Object c = bodyPart.getContent();

        if (c instanceof MimeMessage) {
          attachedMessages.add((Message) c);
        } else if (c instanceof BASE64DecoderStream) {
          // outlook message
          Message m = getOutlookMessage((BASE64DecoderStream) c);
          attachedMessages.add(m);
          // other attachments
        } else if (Part.ATTACHMENT.equals(bodyPart.getDisposition())) {
          try (InputStream is = bodyPart.getInputStream()) {
            MimeMessage m = new MimeMessage(Session.getDefaultInstance(new Properties()),
                bodyPart.getInputStream());
            attachedMessages.add(m);
          } catch (IOException e) {
            LOG.warn("Error getting attached message", e);
          }
        }
      }
    }
    return attachedMessages;
  }


  Message getOutlookMessage(InputStream is) {
    return EmailConverter.outlookMsgToMimeMessage(is);
  }
}
