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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.tschmid.sieve.mock.http.exceptions.HttpException;
import net.tschmid.sieve.mock.http.exceptions.HttpImmutableHeader;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionWriteException;

public class HttpResponse implements AutoCloseable {

  private String status = "200 OK";
  private String contentType = "text/plain";

  private final BufferedOutputStream out;

  private boolean headerSend = false;

  private final Map<String, String> headers = new HashMap<>();

  public HttpResponse(final Socket connection) throws IOException {
    this.out = new BufferedOutputStream(connection.getOutputStream());
  }

  /**
   * Checks if the headers have been sent.
   * @return
   *   true in case the headers are sent other wise false.
   */
  public boolean isHeaderSend() {
    return this.headerSend; 
  }  

  public String getStatus() {
    return this.status;
  }

  public HttpResponse setStatus(final String status) throws HttpException {
    if (this.isHeaderSend())
      throw new HttpImmutableHeader();
      
    this.status = status;
    return this;
  }

  public String getContentType() {
    return this.contentType;
  }

	public HttpResponse setContentType(final String contentType) throws HttpException {
    if (this.isHeaderSend())
      throw new HttpImmutableHeader();
          
    this.contentType = contentType;
    return this;
  }

  /**
   * Flushes the response buffers and sends all currently buffered data.
   * @return
   *   a self reference
   * 
   * @throws ConnectionWriteException
   */
  public HttpResponse flush() throws ConnectionWriteException {
    try {
      this.out.flush();
    } catch (IOException e) {
      throw new ConnectionWriteException(e);
    }
    return this;
  }  

  public HttpResponse sendRaw(final byte[] data) throws ConnectionWriteException {
    try {
      this.out.write(data);
    } catch (IOException e) {
      throw new ConnectionWriteException(e);
    }
    return this;
  }

  public HttpResponse sendRaw(final String data) throws ConnectionWriteException {
    return this.sendRaw(data.getBytes());
  }

  public HttpResponse sendHeader() throws HttpException, ConnectionWriteException{

    if (this.isHeaderSend()) 
      throw new HttpImmutableHeader();

    this.sendRaw(""
      + "HTTP/1.0 "+this.getStatus()+"\r\n"      
      + "Content-Type: "+this.getContentType()+"\r\n"
      + "Date: " + new Date()+"\r\n"
    //  + "Content-length: " + body.length() +"\r\n"
    );

    for (Entry<String, String> item : this.headers.entrySet())
      this.sendRaw(item.getKey()+": "+item.getValue()+"\r\n");
    
    this.sendRaw("\r\n");

    this.headerSend = true;

    return this;
  }

  public HttpResponse sendBody(final String body) throws ConnectionWriteException {
    this.sendRaw(body);
    return this;
  }

  public HttpResponse send(final InputStream stream) throws HttpException, ConnectionWriteException {

    try {
      final BufferedInputStream bis = new BufferedInputStream(stream);
      final ByteArrayOutputStream buf = new ByteArrayOutputStream();

      int result = bis.read();
      while (result != -1) {
        buf.write((byte) result);
        result = bis.read();
      }

      // StandardCharsets.UTF_8.name() > JDK 7
      this.send(buf.toString("UTF-8"));
      
    } catch (IOException e) {
      throw new ConnectionWriteException(e);
    }
    return this;
  }

  public HttpResponse send(final String body) throws HttpException, ConnectionWriteException {

    if (!this.isHeaderSend())
      this.sendHeader();

    this.sendBody(body);

    this.flush();

    return this;
  }  

  @Override
  public void close() throws Exception {
    this.out.close();
  }

  /**
   * Adds a new header to the response.
   * 
   * @param header the header name
   * @param value  the header value
   * 
   * @throws HttpImmutableHeader
   *   in case the header was already transmitted.
   */
  public void addHeader(final String header, final String value) throws HttpImmutableHeader {
    if (this.isHeaderSend()) 
      throw new HttpImmutableHeader();

    this.headers.put(header, value);
  }

}
