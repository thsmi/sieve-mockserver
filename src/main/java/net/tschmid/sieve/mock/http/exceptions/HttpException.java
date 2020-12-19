package net.tschmid.sieve.mock.http.exceptions;

public class HttpException extends Exception {

  private static final long serialVersionUID = -7024702604877147952L;

  private final String status;

  public HttpException(final String status) {
    this.status = status;
  }

  String getStatus() {
    return this.status;
  }
  
}
