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
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.tschmid.sieve.mock.http.endpoints.Endpoint;
import net.tschmid.sieve.mock.log.ActivityLog;

/**
 * Implements a simplistic web server with pluggable endpoints.
 */
public class HttpServer implements Runnable {

  /** The port on which the server listens for incoming requests */
  private static final int port = 8080;
  /** Contains all registered endpoints which can handle incoming request */
  private static final List<Endpoint> endpoints = Collections.synchronizedList(new LinkedList<>());

  /**
   * Starts the web server.
   * It will spawn a worker thread and return immediately.
   * 
   * @return as self reference.
   */
  public HttpServer start() {
    (new Thread(new HttpServer())).start();
    return this;
  }

  /**
   * Registers a new endpoint for the web server.
   * It is threads save and new endpoints can be added at any time.
   */
  public HttpServer addEndpoint(final Endpoint endpoint) {
    HttpServer.endpoints.add(endpoint);
    return this;
  }

  /**
   * Reads data from a resource url.
   * 
   * @param resource
   *   the url to the resource.
   * @return
   *   the resource content as string.
   * @throws IOException
   */
  public String getResource(final String resource) throws IOException {
    return this.getStream(this.getClass().getResource(resource).openStream());
  }

  /**
   * Reads data from a file.
   * 
   * @param path
   *   the path the the file.
   * @return
   *   the file content as string.
   * @throws IOException
   */
  public String getFile(final String path) throws IOException {
    return this.getStream(Files.newInputStream(Paths.get(path)));
  }

  /**
   * Reads data form a stream.
   * 
   * @param stream
   *   the stream to be read.
   * @return 
   *   the stream content as string.
   * @throws IOException 
   */
  public String getStream(final InputStream stream) throws IOException {
    final BufferedInputStream bis = new BufferedInputStream(stream);
    final ByteArrayOutputStream buf = new ByteArrayOutputStream();
    int result = bis.read();
    while (result != -1) {
      buf.write((byte) result);
      result = bis.read();
    }
    // StandardCharsets.UTF_8.name() > JDK 7
    return buf.toString("UTF-8");
  }

  @Override
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
