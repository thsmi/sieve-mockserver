package net.tschmid.sieve.mock.http.exceptions;

public class HttpFileNotFound extends HttpException {

  private static final long serialVersionUID = 2122406293190130154L;
  
  private final String path;
  
  public HttpFileNotFound(final String path) {
    super("404 File not Found");
    this.path = path;    
  }
  
}
