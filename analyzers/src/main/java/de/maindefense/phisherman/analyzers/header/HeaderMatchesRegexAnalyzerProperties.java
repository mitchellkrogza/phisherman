package de.maindefense.phisherman.analyzers.header;

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

  public static class HeaderRegexProperties {
    private String headerName;
    private String pattern;
    private long weight;
    private int order;

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

    public long getWeight() {
      return weight;
    }

    public void setWeight(long weight) {
      this.weight = weight;
    }

    public int getOrder() {
      return order;
    }

    public void setOrder(int order) {
      this.order = order;
    }  
  }  
}
