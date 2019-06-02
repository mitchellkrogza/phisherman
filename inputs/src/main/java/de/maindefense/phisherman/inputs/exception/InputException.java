package de.maindefense.phisherman.inputs.exception;

public class InputException extends Exception{

  public InputException(String message, Throwable cause) {
    super(message, cause);
  }

  public InputException(String message) {
    super(message);
  }

  public InputException(Throwable cause) {
    super(cause);
  }
}
