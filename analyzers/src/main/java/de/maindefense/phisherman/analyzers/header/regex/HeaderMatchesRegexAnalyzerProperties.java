package de.maindefense.phisherman.analyzers.header.regex;

import de.maindefense.phisherman.analyzers.config.AnalyzerProperties;
import java.util.ArrayList;
import java.util.List;

public class HeaderMatchesRegexAnalyzerProperties {

  private List<HeaderRegexProperties> regex = new ArrayList<>();

  public List<HeaderRegexProperties> getRegex() {
    return regex;
  }

  public void setRegex(List<HeaderRegexProperties> regex) {
    this.regex = regex;
  }

  public static class HeaderRegexProperties extends AnalyzerProperties {
    private String headerName;
    private String pattern;

    public String getHeaderName() {
      return headerName;
    }

    public void setHeaderName(String headerName) {
      this.headerName = headerName;
    }

    public String getPattern() {
      return pattern;
    }

    public void setPattern(String pattern) {
      this.pattern = pattern;
    }
  }
}
