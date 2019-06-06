package de.maindefense.phisherman.analyzers.body.url.obfuscated;

import de.maindefense.phisherman.analyzers.Analyzer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ObfuscatedUrlAnalyzer implements Analyzer {

  private static final Logger LOG = LoggerFactory.getLogger(ObfuscatedUrlAnalyzer.class);

  private ObfuscatedUrlAnalyzerProperties properties;

  public ObfuscatedUrlAnalyzer(ObfuscatedUrlAnalyzerProperties properties) {
    this.properties = properties;
  }

  @Override
  public String getAnalyzerName() {
    return ObfuscatedUrlAnalyzer.class.getSimpleName();
  }

  @Override
  public long analyze(MimeMessage message) {
    long result = 0;
    try {
      MimeMessageParser parser = new MimeMessageParser(message);
      parser.parse();
      String htmlContent = parser.getHtmlContent();
      
      if(StringUtils.isEmpty(htmlContent)) {
        return result;
      }
      
      Document html = Jsoup.parse(htmlContent);
      Elements links = html.getElementsByTag("a");
      for (Element link : links) {
        try {
          // check if link text is a URL
          new URL(link.html());

          // link text is a url. link target should be the same then.
          String href = link.attr("href");

          if (!link.html().equalsIgnoreCase(href)) {
            // link text is not equal to the actual URL. this is suspicious

            // check if O365 safe link url
            if (!isValidO365SafeLink(href, link.html())) {
              result += getWeight();
            }
          }
        } catch (MalformedURLException e) {
          LOG.debug("Link text is not an URL. Not unusual then: " + link.html());
        }
      }
    } catch (Exception e) {
      LOG.warn("Error parsing HTML mail content", e);
    }

    return result;
  }

  protected boolean isValidO365SafeLink(String href, String linkText) throws MalformedURLException {
    URL safeLinkUrl = new URL(href);
    String host = safeLinkUrl.getHost();
    if (host.endsWith("outlook.com") || host.endsWith("microsoft.com")) {
      String query = safeLinkUrl.getQuery();
      String[] params = query.split("&");
      for (String param : params) {
        String name = param.split("=")[0];
        String value = param.split("=")[1];
        if (name.equalsIgnoreCase("url")) {
          try {
            String decodedSafeLink = URLDecoder.decode(value, StandardCharsets.UTF_8);
            if (decodedSafeLink.equalsIgnoreCase(linkText)) {
              return true;
            }
          } catch (Exception e) {
            LOG.warn("Cannot decode O365 Safe Link", e);
          }
        }
      }
    }
    return false;
  }

  @Override
  public int getOrder() {
    return properties.getOrder();
  }

  @Override
  public long getWeight() {
    return properties.getWeight();
  }

}
