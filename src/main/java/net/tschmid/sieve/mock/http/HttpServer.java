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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.tschmid.sieve.mock.http.endpoints.Endpoint;
import net.tschmid.sieve.mock.log.ActivityLog;

public class HttpServer implements Runnable {

  private static final int port = 8080;
  private static List<Endpoint> endpoints = new LinkedList<>();

  public HttpServer() {
  }

  public HttpServer start() {
    (new Thread(new HttpServer())).start();
    return this;
  }

  public HttpServer addEndpoint(Endpoint endpoint) {
    HttpServer.endpoints.add(endpoint);
    return this;
  }

  public String getResource(String resource) throws IOException {
    return this.getStream(this.getClass().getResource(resource).openStream());
  }

  public String getFile(String path) throws IOException {
    return this.getStream(Files.newInputStream(Paths.get(path)));
  }

  public String getStream(InputStream stream) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(stream);
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    int result = bis.read();
    while (result != -1) {
      buf.write((byte) result);
      result = bis.read();
    }
    // StandardCharsets.UTF_8.name() > JDK 7
    return buf.toString("UTF-8");
  }

  public void sendError(PrintStream stream, String status) {
    stream.print("HTTP/1.0 " + status + "\r\n\r\n");
  }

  public void sendResponse(PrintStream stream, String contentType, String response) {
    stream.print("" + "HTTP/1.0 200 OK\r\n" + "Content-Type: " + contentType + "\r\n" + "Date: " + new Date() + "\r\n"
        + "Content-length: " + response.length() + "\r\n" + "\r\n" + response);
  }

  public void run() {

    try (ServerSocket socket = new ServerSocket(port)) {

      while (true) {
        (new Thread(new HttpChannel(socket.accept(), HttpServer.endpoints))).start();

        continue;
      }
    } catch (Throwable tr) {
      System.err.println("Could not start server: " + tr);
    }

    ActivityLog.getInstance().log("Server stopped");
  }
}
