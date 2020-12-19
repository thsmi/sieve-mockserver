/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.http.endpoints;

import java.net.URL;

import net.tschmid.sieve.mock.http.HttpRequest;
import net.tschmid.sieve.mock.http.HttpResponse;
import net.tschmid.sieve.mock.http.exceptions.HttpFileNotFound;

/**
 * An endpoint for reading test templates.
 */
public class TemplatesEndpoint implements Endpoint {

  @Override
  public boolean canHandle(HttpRequest request) {

    if (!request.getMethod().equals("GET"))
      return false;

    if (!request.getPath().equals("/template"))
      return false;

    return true;
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) throws Exception {
    String url = "./templates/" + request.getQuery("template") + ".xml";    

    URL resource = this.getClass().getResource("./../" + url);

    if (resource == null)
      throw new HttpFileNotFound(url);      

    response.send(resource.openStream());
  }
  
}
