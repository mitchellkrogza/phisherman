package de.maindefense.phisherman.inputs.exception;

public class InputException extends Exception{

  private static final long serialVersionUID = 5807214401681811699L;

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
