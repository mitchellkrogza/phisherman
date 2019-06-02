package de.maindefense.phisherman.inputs;

import de.maindefense.phisherman.common.FileSystemDataProvider;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Input {

  static String INPUT_DIRECTORY_NAME = "INPUT";
  
  void fetchInput();

  String getInputName();

  default Path getInputDirectoryPath(FileSystemDataProvider fileSystemDataProvider) {
    return Paths.get(fileSystemDataProvider.getDataDir().toString(), INPUT_DIRECTORY_NAME, getInputName());
  }
}
