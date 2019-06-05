package de.maindefense.phisherman.analyzers.header.compare;

import java.util.ArrayList;
import java.util.List;

public class HeaderCompareAnalyzerProperties {

  private List<HeaderCompareProperties> compare = new ArrayList<>();

  public List<HeaderCompareProperties> getCompare() {
    return compare;
  }

  public void setCompare(List<HeaderCompareProperties> compareProperties) {
    this.compare = compareProperties;
  }

  public static class HeaderCompareProperties {
    private String headerName1;
    private String headerName2;
    private HeaderCompareOperator operator;
    private long weight;
    private int order;

    public String getHeaderName1() {
      return headerName1;
    }

    public void setHeaderName1(String headerName1) {
      this.headerName1 = headerName1;
    }

    public String getHeaderName2() {
      return headerName2;
    }

    public void setHeaderName2(String headerName2) {
      this.headerName2 = headerName2;
    }

    public HeaderCompareOperator getOperator() {
      return operator;
    }

    public void setOperator(HeaderCompareOperator operator) {
      this.operator = operator;
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

    public static enum HeaderCompareOperator {
      EQUALS, CONTAINS, NOT_EQUALS, NOT_CONTAINS
    }
  }
}
