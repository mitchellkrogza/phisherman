package de.maindefense.phisherman.analyzers;

import javax.mail.Message;

public interface Analyzer {

  String getAnalyzerName();

  long analyze(Message message);

  int getOrdinal();

  long getWeight();

}
