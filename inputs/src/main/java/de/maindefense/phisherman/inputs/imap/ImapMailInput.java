package de.maindefense.phisherman.inputs.imap;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPNestedMessage;
import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.Input;
import de.maindefense.phisherman.inputs.exception.InputException;
import de.maindefense.phisherman.inputs.imap.ImapInputProperties.ImapProperties;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ImapMailInput implements Input {

  private static final Logger LOG = LoggerFactory.getLogger(ImapMailInput.class);
  /**
   * The Constant MAIL_STORE_PROTOCOL_IMAPS defines imaps as only supported mail store protocol.
   * Unencrypted imap is not supported.
   */
  private static final String MAIL_STORE_PROTOCOL_IMAPS = "imaps";
  private static final String INPUT_NAME_PREFIX = "imap";

  private ImapProperties imapProperties;
  private FileSystemDataProvider fileSystemDataProvider;



  public ImapMailInput(ImapProperties imapProperties,
      FileSystemDataProvider fileSystemDataProvider) {
    this.imapProperties = imapProperties;
    this.fileSystemDataProvider = fileSystemDataProvider;
  }


  @Scheduled(fixedDelay = 60000)
  @Override
  public void fetchInput() {
    try (Store store = getImapStore();
        IMAPFolder folder = (IMAPFolder) store.getFolder(imapProperties.getFolder())) {
      // open the store, so we can write to it (set messages deleted after fetching)
      if (!folder.isOpen()) {
        folder.open(Folder.READ_WRITE);
      }
      Message[] messages = folder.getMessages();
      for (int i = 0; i < messages.length; i++) {
        Message msg = messages[i];
        try {
          writeMessageToLocalFileSystem(msg);
          // set deleted, only if message has been written to local file system successfully
          msg.setFlag(Flag.DELETED, true);
        } catch (InputException e) {
          LOG.error("Message could not be written. Will be retried on next fetch attempt.", e);
        }
      }
    } catch (MessagingException e) {
      LOG.error("Error fetching messages from server.", e);
    }
  }

  protected void writeMessageToLocalFileSystem(Message message) throws InputException {
    try (OutputStream os = Files.newOutputStream(getPathToWriteMessage())) {
      message.writeTo(os);
    } catch (Exception e) {
      throw new InputException("Error storing message to local file system", e);
    }
  }

  protected Path getPathToWriteMessage() {
    return Paths.get(fileSystemDataProvider.getDataDir().toString(), getInputName(),
        UUID.randomUUID().toString());

  }

  protected Store getImapStore() throws MessagingException {
    Properties properties = new Properties();
    properties.put("mail.store.protocol", MAIL_STORE_PROTOCOL_IMAPS);
    properties.put("mail.imaps.host", imapProperties.getHostname());
    properties.put("mail.imaps.port", imapProperties.getPort());
    Session session = Session.getDefaultInstance(properties, null);
    Store store = session.getStore(MAIL_STORE_PROTOCOL_IMAPS);
    store.connect(imapProperties.getHostname(), imapProperties.getPort(),
        imapProperties.getUsername(), imapProperties.getPassword());
    return store;
  }



  @Override
  public String getInputName() {
    return INPUT_NAME_PREFIX + "_" + imapProperties.getHostname();
  }

}
