package de.maindefense.phisherman.analyzers.header.regex;

import de.maindefense.phisherman.analyzers.Analyzer;
import de.maindefense.phisherman.analyzers.header.regex.HeaderMatchesRegexAnalyzerProperties.HeaderRegexProperties;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderMatchesRegexAnalyzer implements Analyzer {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderMatchesRegexAnalyzer.class);

  private String headerName;
  private Pattern pattern;
  private long weight;
  private int order;

  public HeaderMatchesRegexAnalyzer(HeaderRegexProperties properties)
      throws PatternSyntaxException {
    this.headerName = properties.getHeaderName();
    this.pattern = Pattern.compile(properties.getPattern());
    this.weight = properties.getWeight();
    this.order = properties.getOrder();
  }

  @Override
  public String getAnalyzerName() {
    return "header_regex_" + toString();
  }

  @Override
  public long analyze(MimeMessage message) {
    int result = 0;

    try {
      String[] values = message.getHeader(headerName);
      LOG.info(String.format("Header [%s] has the following values: %s", headerName,
          Arrays.toString(values)));
      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          Matcher matcher = pattern.matcher(values[i]);
          if (matcher.find()) {
            LOG.info(String.format(
                "Header [%s] with value [%s] matches pattern %s. Adding %d to threat score.",
                headerName, values[i], pattern.toString(), getWeight()));
            result += getWeight();
          }
        }
      }
    } catch (MessagingException e) {
      LOG.error("Error regex analyzing header: " + toString(), e);
    }
    return result;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String toString() {
    return "HeaderMatchesRegexAnalyzer [headerName=" + headerName + ", regex=" + pattern
        + ", weight=" + weight + ", order=" + order + "]";
  }

  @Override
  public long getWeight() {
    return weight;
  }

}
