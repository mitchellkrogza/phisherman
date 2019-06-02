package de.maindefense.phisherman.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.env.Environment;

public class FileSystemDataProvider {

  private Environment env;
  
  public FileSystemDataProvider(Environment env) {
    this.env = env;
  }
  
  public Path getDataDir() {
    return Paths.get(env.getProperty(PropertyNames.PROPERTY_NAME_DATADIR, "data"));
  }

}
