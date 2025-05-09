package model.exception;

public class ClientSideException extends MyException { // exception used in client
  public ClientSideException() {}

  public ClientSideException(String message) { super(message); }

  public ClientSideException(String message, Throwable cause) { super(message, cause); }
}
