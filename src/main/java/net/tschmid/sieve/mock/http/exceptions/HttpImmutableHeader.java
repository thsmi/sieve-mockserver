package net.tschmid.sieve.mock.http.exceptions;

public class HttpImmutableHeader extends HttpException {

  private static final long serialVersionUID = 7566873849239449036L;

  public HttpImmutableHeader() {
    super("500 Internal Server Error");
  }
}
