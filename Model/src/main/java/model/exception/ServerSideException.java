package model.exception;

public class ServerSideException extends MyException { // exception used in server
  public ServerSideException() {}

  public ServerSideException(String message) { super(message); }

  public ServerSideException(String message, Throwable cause) { super(message, cause); }
}
