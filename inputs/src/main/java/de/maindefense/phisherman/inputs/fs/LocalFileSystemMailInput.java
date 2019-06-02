package de.maindefense.phisherman.inputs.fs;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.AbstractInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystemMailInput extends AbstractInput {

  private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemMailInput.class);

  private static final String INPUT_NAME_PREFIX = "fs";

  private FileSystemDataProvider fileSystemDataProvider;

  private Path sourcePath;

  public LocalFileSystemMailInput(Path sourcePath,
      FileSystemDataProvider fileSystemDataProvider) {
    this.sourcePath = sourcePath;
    this.fileSystemDataProvider = fileSystemDataProvider;
  }

  @Override
  public void fetchInput() {
    Session session = Session.getDefaultInstance(new Properties());
    try {
      Files.list(sourcePath).filter(p->!Files.isDirectory(p)).forEach(p->{
        try (InputStream is = Files.newInputStream(p)){
          MimeMessage msg = new MimeMessage(session, is);
          writeMessageToLocalFileSystem(msg, fileSystemDataProvider);
          Files.deleteIfExists(p);
        } catch (Exception e) {
          LOG.error("Message could not be written. Will be retried on next fetch attempt.", e);
        }
      });
    } catch (IOException e) {
      LOG.error("Error fetching mesages from local file system. Will be retried on next fetch attempt.", e);
    }
  }

  @Override
  public String getInputName() {
    return INPUT_NAME_PREFIX + "_" + sourcePath.hashCode();
  }

}
