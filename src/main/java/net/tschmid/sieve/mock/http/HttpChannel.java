/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

import net.tschmid.sieve.mock.http.endpoints.Endpoint;

public class HttpChannel implements Runnable {

  /** The connection to the remote socket */
  private final Socket connection;
  /** The list with all configured endpoints */
  private final List<Endpoint> endpoints;

  /**
   * Creates a new http channel
   * @param connection
   *   the incoming connection
   * @param endpoints
   *   a list with all configured endpoints.
   */
  public HttpChannel(final Socket connection, final List<Endpoint> endpoints) {
    this.connection = connection;
    this.endpoints = endpoints;
  }

  @Override
  public void run() {
    try (
      final HttpRequest request = new HttpRequest(connection); 
      final HttpResponse response = new HttpResponse(connection)) 
    {

      request.getHeader();

      try {
        for (final Endpoint endpoint : this.endpoints) {
          if (!endpoint.canHandle(request))
            continue;

          endpoint.handle(request, response);
          return;
        }
      } catch (Exception ex) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        response
          .setStatus("500 Internal Server Error")
          .send(sw.toString());
          
        return;
      }

      response
        .setStatus("404 File Not Found")
        .sendHeader();

      // TODO a delete on / should stop the server.
      /*
       * processEndpoints((new HttpRequest(in)).getHeader()).send(pout); continue;
       * 
       * 
       * if (!this.endpoint.canHandle(request);
       * 
       * // TODO a delete on / should stop the server. processEndpoints((new
       * HttpRequest(in)).getHeader()).send(pout);
       */
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
