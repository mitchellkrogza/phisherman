package de.maindefense.phisherman.analyzers;

import javax.mail.internet.MimeMessage;

public interface Analyzer {

  String getAnalyzerName();

  long analyze(MimeMessage message);

  int getOrder();

  long getWeight();

}
