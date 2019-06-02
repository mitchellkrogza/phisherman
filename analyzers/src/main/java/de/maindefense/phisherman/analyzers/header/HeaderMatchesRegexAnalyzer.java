package de.maindefense.phisherman.analyzers.header;

import de.maindefense.phisherman.analyzers.Analyzer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderMatchesRegexAnalyzer implements Analyzer {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderMatchesRegexAnalyzer.class);

  private String headerName;
  private Pattern regex;
  private long weight;
  private int oridinal;

  public HeaderMatchesRegexAnalyzer(String headerName, String regex, long weight, int oridinal)
      throws PatternSyntaxException {
    this.headerName = headerName;
    this.regex = Pattern.compile(regex);
    this.weight = weight;
    this.oridinal = oridinal;
  }

  @Override
  public String getAnalyzerName() {
    return "header_regex";
  }

  @Override
  public long analyze(Message message) {
    int result = 0;

    try {
      String[] values = message.getHeader(headerName);
      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          Matcher matcher = regex.matcher(values[i]);
          if (matcher.find()) {
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
  public int getOrdinal() {
    return oridinal;
  }

  @Override
  public String toString() {
    return "HeaderMatchesRegexAnalyzer [headerName=" + headerName + ", regex=" + regex + ", weight="
        + weight + ", oridinal=" + oridinal + "]";
  }

  @Override
  public long getWeight() {
    return weight;
  }

}
