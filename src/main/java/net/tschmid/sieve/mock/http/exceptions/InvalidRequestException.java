package net.tschmid.sieve.mock.http.exceptions;

import java.io.UnsupportedEncodingException;

public class InvalidRequestException extends HttpException {

  private static final long serialVersionUID = 6062058474050956885L;

  public InvalidRequestException(final String message) {
    super("400 Bad Request - " + message);
  }

  public InvalidRequestException(final String message, final UnsupportedEncodingException e) {
    this(message);
    e.printStackTrace();
  }
}
