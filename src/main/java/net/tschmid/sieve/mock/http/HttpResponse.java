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

  private final BufferedOutputStream out;

  private String status = "200 OK";
  private String contentType = "text/plain";
  private boolean headerSend = false;

  private Map<String, String> headers = new HashMap<>();

  public HttpResponse(Socket connection) throws IOException {
    this.out = new BufferedOutputStream(connection.getOutputStream());
  }

  public boolean isHeaderSend() {
    return this.headerSend; 
  }  

  public String getStatus() {
    return this.status;
  }

  public HttpResponse setStatus(String status) throws HttpException {
    if (this.isHeaderSend())
      throw new HttpImmutableHeader();
      
    this.status = status;
    return this;
  }

  public String getContentType() {
    return this.contentType;
  }

	public HttpResponse setContentType(String contentType) throws HttpException {
    if (this.isHeaderSend())
      throw new HttpImmutableHeader();
          
    this.contentType = contentType;
    return this;
  }

  public HttpResponse flush() throws ConnectionWriteException {
    try {
      this.out.flush();
    } catch (IOException e) {
      throw new ConnectionWriteException(e);
    }
    return this;
  }  

  public HttpResponse sendRaw(byte[] data) throws ConnectionWriteException {
    try {
      this.out.write(data);
    } catch (IOException e) {
      throw new ConnectionWriteException(e);
    }
    return this;
  }

  public HttpResponse sendRaw(String data) throws ConnectionWriteException {
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

  public HttpResponse sendBody(String body) throws ConnectionWriteException {
    this.sendRaw(body);
    return this;
  }

  public HttpResponse send(InputStream stream) throws HttpException, ConnectionWriteException {

    try {
      BufferedInputStream bis = new BufferedInputStream(stream);
      ByteArrayOutputStream buf = new ByteArrayOutputStream();

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

  public HttpResponse send(String body) throws HttpException, ConnectionWriteException {

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

  public void addHeader(String header, String value) {
    this.headers.put(header, value);
  }

}