package de.maindefense.phisherman.analyzers;

import com.sun.mail.imap.IMAPNestedMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AttachedMessageProvider {

  public List<Message> getAttachedMessages(Message message) throws IOException, MessagingException {
    Object content = message.getContent();
    List<Message> attachedMessages = new ArrayList<>();

    if (content instanceof Multipart) {
      Multipart multipart = (Multipart) content;
      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
            && StringUtils.isEmpty(bodyPart.getFileName())
            && !(bodyPart.getContent() instanceof IMAPNestedMessage)) {
          continue; // dealing with attachments only
        }
        InputStream is = bodyPart.getInputStream();
        try {
          attachedMessages.add(new MimeMessage(Session.getDefaultInstance(new Properties()), is));
        } catch (MessagingException e) {
          e.printStackTrace();
        }
      }
    }
    return attachedMessages;
  }
}
