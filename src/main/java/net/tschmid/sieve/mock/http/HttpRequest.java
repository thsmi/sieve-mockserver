/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.tschmid.sieve.mock.http.exceptions.InvalidRequestException;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionClosedException;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionException;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionReadException;

/**
 * Implements an http request which reads directly from the socket.
 */
public class HttpRequest implements AutoCloseable {

  /** Stores the http headers after parsing completes */
  private final Map<String, String> header = new HashMap<>();
  /** Stores the query string after parsing completes */
  private final Map<String, String> query = new HashMap<>();

  /** The socket's input stream, buffered for better performance */
  private final BufferedInputStream in;

  /** The http method like GET, POST etc.. */
  private String method;
  /** The path which was extracted from the query string */
  private String path;
  /** The requests http protocol version */
  private String protocol;

  /**
   * Creates a new instance.
   * @param connection
   *   the socket from which incoming data should be read.
   * @throws IOException
   */
  public HttpRequest(final Socket connection) throws IOException {
    this.in = new BufferedInputStream(connection.getInputStream());
  }

  /**
   * Returns the request's http method as string, e.g. GET, POST, etc...
   * @return
   *   the request method as string.
   */
  public String getMethod() {
    return this.method;
  }

  public String getPath() {
    return this.path;
  }

  /**
   * Returns the http protocol used for this request.
   * @return
   *   the protocol as string.
   */
  public String getProtocol() {
    return this.protocol;
  }

  public boolean hasQuery(String key) {
    return this.query.containsKey(key);
  }

  public String getQuery(String key) {
    return this.query.get(key);
  }

  /**
   * Checks if the given header exists.
   * It will only succeed after the header is parsed.
   * 
   * @param name
   *   the header name to be checked.
   * @return
   *   true in case a header exists otherwise false.
   */
  public boolean hasHeader(String name) {
    return this.header.containsKey(name);
  }

  /**
   * Returns the given header in case it exists.
   * 
   * @param name
   *   the header name to be returned .
   * @return
   *   the header value as string or null in case the header does not exits.
   */
  public String getHeader(String name) {
    return this.header.get(name);
  }

  protected String[] split(String data, String delim, int count) {

    if (data == null)
      return new String[0];

    return data.split(delim, 3);
  }

  public HttpRequest getHeader() throws InvalidRequestException, ConnectionException {
    String[] request = this.split(this.readLine(), " ", 3);

    if (request.length != 3)
      throw new InvalidRequestException("Invalid Request Line");

    this.method = request[0];

    try {

      String[] path = request[1].split("\\?", 2);
      this.path = path[0];

      this.query.clear();

      if (path.length > 1) {
        String[] query = path[1].split("&");

        for (String item : query) {
          String[] tmp = item.split("=");

          String key = java.net.URLDecoder.decode(tmp[0], StandardCharsets.UTF_8.name());
          String value = java.net.URLDecoder.decode((tmp.length > 1 ? tmp[1] : ""), StandardCharsets.UTF_8.name());

          this.query.put(key, value);
        }
      }
    } catch (UnsupportedEncodingException e) {
      throw new InvalidRequestException("Request path is invalid", e);
    } 
    
    this.protocol = request[2];

    this.header.clear();
    while (true) {
      String[] header = this.split(this.readLine(), ":", 2);
      
      if (header[0].equals(""))
        break;

      this.header.put(header[0], header[1].trim());
    }

    return this;
  }

  public String getBody() throws Exception {
    String length = this.header.get("Content-Length");

    if (length == null || length.equals(""))
      throw new Exception("No Content length specified.");
    
    byte buffer[] = new byte[4096];
    String data = "";

    int count = Integer.parseInt(length.trim());
    while (count > 0) {    
      int read = this.in.read(buffer, 0, Math.min(count, buffer.length));
      
      if (read > 0)
        data += new String(buffer, 0, read);  

      if (read == -1)
        throw new Exception("Reached end of stream");

      count -= read;
    }

    return data;
  }

  public String readLine() throws ConnectionReadException {

    try {

      StringBuffer buffer = new StringBuffer();

      int data;

      while (true) {

        data = this.in.read();

        if (data != '\r') {
          buffer.append((char) data);
          continue;
        }

        data = this.in.read();

        if (data != '\n') {
          buffer.append((char) '\r');
          buffer.append((char) data);
          continue;
        }

        break;
      }

      return buffer.toString();
    } catch (IOException e) {
      throw new ConnectionReadException(e);
    }
  }

  public byte[] read(int len) throws ConnectionException {

    byte[] buffer = new byte[len];
    int read = 0;

    try {
      read = this.in.read(buffer);
    } catch (IOException e) {
      throw new ConnectionReadException(e);
    }

    if (read != len)
      throw new ConnectionClosedException();

    return buffer;
  }

  @Override
  public void close() throws Exception {
    this.in.close();
  }
}
