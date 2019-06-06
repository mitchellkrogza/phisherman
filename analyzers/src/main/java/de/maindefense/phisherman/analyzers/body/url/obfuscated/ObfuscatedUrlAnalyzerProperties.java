package de.maindefense.phisherman.analyzers.body.url.obfuscated;

import de.maindefense.phisherman.analyzers.config.AnalyzerProperties;

public class ObfuscatedUrlAnalyzerProperties extends AnalyzerProperties{

  private boolean enabled;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
