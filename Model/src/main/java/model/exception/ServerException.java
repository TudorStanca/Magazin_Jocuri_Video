package model.exception;

public class ServerException extends MyException { // used when creating a server
  public ServerException() {}

  public ServerException(String message) { super(message); }

  public ServerException(String message, Throwable cause) { super(message, cause); }
}
