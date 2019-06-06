package de.maindefense.phisherman.analyzers.config;

public abstract class AnalyzerProperties {

  private long weight;
  private int order;

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
