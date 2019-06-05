package de.maindefense.phisherman.analyzers.header.compare;

import de.maindefense.phisherman.analyzers.Analyzer;
import de.maindefense.phisherman.analyzers.header.compare.HeaderCompareAnalyzerProperties.HeaderCompareProperties;
import de.maindefense.phisherman.analyzers.header.compare.HeaderCompareAnalyzerProperties.HeaderCompareProperties.HeaderCompareOperator;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderCompareAnalyzer implements Analyzer {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderCompareAnalyzer.class);

  private String headerName1;
  private String headerName2;
  private HeaderCompareOperator operator;
  private long weight;
  private int order;

  public HeaderCompareAnalyzer(HeaderCompareProperties p) {
    this.headerName1 = p.getHeaderName1();
    this.headerName2 = p.getHeaderName2();
    this.operator = p.getOperator();
    this.weight = p.getWeight();
    this.order = p.getOrder();
  }

  @Override
  public String getAnalyzerName() {
    return "header_compare_" + toString();
  }

  @Override
  public long analyze(Message message) {
    long result = 0;

    try {
      String[] headers1 = message.getHeader(headerName1);
      String[] headers2 = message.getHeader(headerName2);

      boolean equals = false;
      boolean contains = false;

      if (headers1 != null && headers2 != null) {
        for (int i = 0; i < headers1.length; i++) {
          for (int j = 0; j < headers2.length; j++) {
            if (headers1[i].equals(headers2[j])) {
              equals = true;
            }
            if (headers1[i].contains(headers2[j])) {
              contains = true;
            }
          }
        }

        switch (operator) {
          case EQUALS:
            if (equals) {
              result += getWeight();
            }
            break;
          case NOT_EQUALS:
            if (!equals) {
              result += getWeight();
            }
            break;
          case CONTAINS:
            if (contains) {
              result += getWeight();
            }
            break;
          case NOT_CONTAINS:
            if (!contains) {
              result += getWeight();
            }
            break;
          default:
            break;
        }
      }
    } catch (MessagingException e) {
      LOG.error("Error compare analyzing header: " + toString(), e);
    }
    return result;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public long getWeight() {
    return weight;
  }

  @Override
  public String toString() {
    return "HeaderCompareAnalyzer [headerName1=" + headerName1 + ", headerName2=" + headerName2
        + ", operator=" + operator + ", weight=" + weight + ", order=" + order + "]";
  }

}
