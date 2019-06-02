package de.maindefense.phisherman.inputs.fs;

import java.util.ArrayList;
import java.util.List;

public class LocalFimeSystemInputProperties {

  private List<String> paths = new ArrayList<>();

  public List<String> getPaths() {
    return paths;
  }

  public void setPaths(List<String> paths) {
    this.paths = paths;
  }

}
