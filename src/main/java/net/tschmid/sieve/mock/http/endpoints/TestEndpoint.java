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
import net.tschmid.sieve.mock.http.exceptions.HttpInternalServerError;
import net.tschmid.sieve.mock.tests.TestRunner;

public class TestEndpoint implements Endpoint {

  @Override
  public boolean canHandle(HttpRequest request) {
    if (request.getPath().equals("/test"))
      return true;

    return false;
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) throws Exception {
    
    // Get the current status
    if (request.getMethod().equals("GET")) {
      response
        .setContentType("application/json")
        .send("{ \"running\" : " + (TestRunner.getInstance().isRunning() ? "true" : "false") + " }");

      return;
    }

    // Start a test
    if (request.getMethod().equals("PUT")) {

      TestRunner.getInstance().start(request.getBody());

      response
        .setContentType("application/json")
        .send("{ \"running\" : " + (TestRunner.getInstance().isRunning() ? "true" : "false") + " }");

      return;
    }

    // Stop a test
    if (request.getMethod().equals("DELETE")) {

      TestRunner.getInstance().stop();

      response
        .setContentType("application/json")
        .send("{ \"running\" : " + (TestRunner.getInstance().isRunning() ? "true" : "false") + " }");

      return;
    }

    throw new HttpInternalServerError("500 Internal Server Error");
  }
}
