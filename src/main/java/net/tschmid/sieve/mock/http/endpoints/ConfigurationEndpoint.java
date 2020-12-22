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
import net.tschmid.sieve.mock.http.exceptions.HttpFileNotFound;
import net.tschmid.sieve.mock.tests.TestRunner;

public class ConfigurationEndpoint implements Endpoint {

  @Override
  public boolean canHandle(final HttpRequest request) {
    if (request.getPath().equals("/flags"))
      return true;

    if (request.getPath().startsWith("/flags/"))
      return true;

    return false;
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) throws Exception {

    if (request.getMethod().equals("GET") && request.getPath().equals("/flags")) {
      response
         .setContentType("application/json")
         .send(TestRunner.getInstance().getFlags().toJson());

      return;
    }

    if (request.getMethod().equals("POST") && request.getPath().startsWith("/flags/")) {

      TestRunner.getInstance().getFlags()
        .setFlag(request.getPath().substring("/flags/".length()), request.getBody());

      response
        .setContentType("application/json")
        .send(TestRunner.getInstance().getFlags().toJson());
    }

    throw new HttpFileNotFound(request.getPath());
  }
  
}
