package de.maindefense.phisherman.inputs;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import de.maindefense.phisherman.inputs.exception.InputException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.mail.Message;

public abstract class AbstractInput implements Input {

  protected void writeMessageToLocalFileSystem(Message message,
      FileSystemDataProvider fileSystemDataProvider) throws InputException {
    Path pathToWriteMessage = getPathToWriteMessageTo(fileSystemDataProvider);
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
   * @param fileSystemDataProvider the file system data provider
   * 
   * @return the path to write message to
   */
  protected Path getPathToWriteMessageTo(FileSystemDataProvider fileSystemDataProvider) {
    return Paths.get(getInputDirectoryPath(fileSystemDataProvider).toString(),
        UUID.randomUUID().toString());
  }

}
