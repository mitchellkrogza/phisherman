package de.maindefense.phisherman.inputs.imap;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.Input;
import de.maindefense.phisherman.inputs.exception.InputException;
import de.maindefense.phisherman.inputs.imap.ImapInputProperties.ImapServerProperties;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImapMailInput implements Input {

  private static final Logger LOG = LoggerFactory.getLogger(ImapMailInput.class);
  /**
   * The Constant MAIL_STORE_PROTOCOL_IMAPS defines imaps as only supported mail store protocol.
   * Unencrypted imap is not supported.
   */
  private static final String MAIL_STORE_PROTOCOL_IMAPS = "imaps";
  private static final String INPUT_NAME_PREFIX = "imap";

  private ImapServerProperties imapProperties;
  private FileSystemDataProvider fileSystemDataProvider;



  public ImapMailInput(ImapServerProperties imapProperties,
      FileSystemDataProvider fileSystemDataProvider) {
    this.imapProperties = imapProperties;
    this.fileSystemDataProvider = fileSystemDataProvider;
  }


  @Override
  public void fetchInput() {
    try (Store store = getImapStore();
        Folder folder = store.getFolder(imapProperties.getFolder())) {
      // open the store, so we can write to it (set messages deleted after fetching)

      if (!folder.isOpen()) {
        folder.open(Folder.READ_WRITE);
      }
      // get the messages and save them to the local data directory
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
    Path pathToWriteMessage = getPathToWriteMessageTo();
    try {
      Files.createDirectories(pathToWriteMessage.getParent());
      Files.createFile(pathToWriteMessage);
    } catch (IOException e) {
      throw new InputException("Error storing message to local file system", e);
    }

    try (OutputStream os = Files.newOutputStream(pathToWriteMessage)) {
      message.writeTo(os);
    } catch (Exception e) {
      throw new InputException("Error storing message to local file system", e);
    }
  }

  /**
   * Gets the path to write message to.
   * 
   * File name is randomly generated. Subject/Sender as file name would be more human-readable, but
   * increases the risk of messing around with file name vulnerabilities.
   *
   * @return the path to write message to
   */
  protected Path getPathToWriteMessageTo() {
    return Paths.get(getInputDirectoryPath(fileSystemDataProvider).toString(),
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
