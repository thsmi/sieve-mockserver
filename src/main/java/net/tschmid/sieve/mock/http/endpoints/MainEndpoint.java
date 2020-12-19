/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.http.endpoints;

import net.tschmid.sieve.mock.http.HttpRequest;
import net.tschmid.sieve.mock.http.HttpResponse;

public class MainEndpoint implements Endpoint {

  public MainEndpoint() {
  }

  @Override
  public boolean canHandle(HttpRequest request) {

    if (request.getMethod().equals("GET") != true)
      return false;

    if (request.getPath().equals("/"))
      return true;

    if (request.getPath().equals("/index.html"))
      return true;

    if (request.getPath().equals("/main.mjs"))
      return true;

    return false;
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
    String file = request.getPath();

    if (file.equals("/"))
      file = "/index.html";

    if (file.endsWith(".html"))
      response.setContentType("text/html");

    if (file.endsWith(".mjs"))
      response.setContentType("text/javascript");
      
    response.send(
      this.getClass().getResource("./../"+file).openStream());
  }

}