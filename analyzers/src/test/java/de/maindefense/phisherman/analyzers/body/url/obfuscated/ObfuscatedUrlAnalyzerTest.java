package de.maindefense.phisherman.analyzers.body.url.obfuscated;

import static org.junit.jupiter.api.Assertions.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObfuscatedUrlAnalyzerTest {

  private static final long WEIGHT = 100;
  private ObfuscatedUrlAnalyzer analyzer;

  @BeforeEach
  void setUp() throws Exception {
    ObfuscatedUrlAnalyzerProperties properties = new ObfuscatedUrlAnalyzerProperties();
    properties.setWeight(WEIGHT);
    analyzer = new ObfuscatedUrlAnalyzer(properties);
  }

  @Test
  void testAnalyzeNoHTML() throws AddressException, MessagingException {
    MimeMessage message = dummyMessage();
    message.setText("This is actual message");
    long result = analyzer.analyze(message);
    assertEquals(0, result);
  }

  @Test
  void testAnalyzeSimpleHtmlNoLinkURL() throws MalformedURLException, EmailException {
    HtmlEmail email = dummyHtmlMail();

    // embed the image and get the content id
    URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
    String cid = email.embed(url, "Apache logo");

    // set the html message
    email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid
        + "\"><a href='http://foo.bar'>Link text is no URL</a></html>");

    // set the alternative message
    email.setTextMsg("Your email client does not support HTML messages");
    email.buildMimeMessage();

    long result = analyzer.analyze(email.getMimeMessage());
    assertEquals(0, result);
  }

  @Test
  void testAnalyzeSimpleHtmlMultipleURLs() throws MalformedURLException, EmailException {
    HtmlEmail email = dummyHtmlMail();

    // embed the image and get the content id
    URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
    String cid = email.embed(url, "Apache logo");

    // set the html message
    email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid
        + "\"><a href='http://foo.bar'>https://a.b</a><a href='http://foo.bar2'>https://a.b.c?asa=wer</a></html>");

    // set the alternative message
    email.setTextMsg("Your email client does not support HTML messages");
    email.buildMimeMessage();

    long result = analyzer.analyze(email.getMimeMessage());
    assertEquals(2 * WEIGHT, result);
  }

  @Test
  void testAnalyzeSimpleHtml() throws MalformedURLException, EmailException {
    HtmlEmail email = dummyHtmlMail();

    // embed the image and get the content id
    URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
    String cid = email.embed(url, "Apache logo");

    // set the html message
    email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid
        + "\"><a href='http://foo.bar'>https://a.b</a></html>");

    // set the alternative message
    email.setTextMsg("Your email client does not support HTML messages");
    email.buildMimeMessage();

    long result = analyzer.analyze(email.getMimeMessage());
    assertEquals(WEIGHT, result);
  }

  @Test
  void testIsValidO365SafeLinkValid() throws MalformedURLException {
    String testUrl =
        "https://na01.safelinks.protection.outlook.com/?url=https%3a%2f%2fmaindefense.de";
    boolean valid = analyzer.isValidO365SafeLink(testUrl, "https://maindefense.de");
    assertTrue(valid);
  }

  @Test
  void testIsValidO365SafeLinkValidNoMicrosoftLink() throws MalformedURLException {
    String testUrl =
        "https://na01.safelinks.protection.outlook.com.foo/?url=https%3a%2f%2fmaindefense.de";
    boolean valid = analyzer.isValidO365SafeLink(testUrl, "https://maindefense.de");
    assertFalse(valid);
  }

  @Test
  void testIsValidO365SafeLinkInvalid() throws MalformedURLException {
    String testUrl =
        "https://na01.safelinks.protection.outlook.com/?url=https%3a%2f%2fmaindefense.de";
    boolean valid = analyzer.isValidO365SafeLink(testUrl, "https://maindefense.de?foo=bar");
    assertFalse(valid);
  }

  private Session dummySession() {
    return Session.getDefaultInstance(new Properties());
  }

  private HtmlEmail dummyHtmlMail() throws EmailException {
    HtmlEmail email = new HtmlEmail();
    email.setHostName("mail.myserver.com");
    email.addTo("jdoe@somewhere.org", "John Doe");
    email.setFrom("me@apache.org", "Me");
    email.setSubject("Test email with inline image");
    return email;
  }

  private MimeMessage dummyMessage() throws AddressException, MessagingException {
    MimeMessage message = new MimeMessage(dummySession());
    message.setFrom(new InternetAddress("from@bar.baz"));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress("to@bar.baz"));
    message.setSubject("This is the Subject Line");
    return message;
  }

}
