package de.maindefense.phisherman.inputs.fs;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.AbstractInput;
import de.maindefense.phisherman.inputs.exception.InputException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystemMailInput extends AbstractInput {

  private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemMailInput.class);

  private static final String INPUT_NAME_PREFIX = "fs";

  private FileSystemDataProvider fileSystemDataProvider;

  private Path sourcePath;

  public LocalFileSystemMailInput(Path sourcePath, FileSystemDataProvider fileSystemDataProvider) {
    this.sourcePath = sourcePath;
    this.fileSystemDataProvider = fileSystemDataProvider;
  }

  @Override
  public void fetchInput() {
    walkRecursiveStartingFrom(p -> {
      try {
        Message msg = getMessageFromLocalFileSystemPath(p);
        writeMessageToLocalFileSystem(msg, fileSystemDataProvider);
        Files.deleteIfExists(p);
      } catch (InputException | IOException e) {
        LOG.error("Message could not be written. Will be retried on next fetch attempt.", e);
      }
    });
  }

  @Override
  public String getInputName() {
    return INPUT_NAME_PREFIX + "_" + sourcePath.hashCode();
  }

  public void walkRecursiveStartingFrom(Consumer<Path> function) {
    try (Stream<Path> stream = Files.walk(sourcePath)) {
      stream.filter(p -> !Files.isDirectory(p)).forEach(p -> {
        function.accept(p);
      });
    } catch (IOException e) {
      LOG.error(
          "Error fetching mesages from local file system. Will be retried on next fetch attempt.",
          e);
    }
  }

  public Message getMessageFromLocalFileSystemPath(Path path) throws InputException {
    Session session = Session.getDefaultInstance(new Properties());
    try (InputStream is = Files.newInputStream(path)) {
      return new MimeMessage(session, is);
    } catch (IOException | MessagingException e) {
      throw new InputException("Error getting message from local file system path", e);
    }
  }

}
