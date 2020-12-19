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

/**
 * Implements an http endpoint.
 */
public interface Endpoint {
  /**
   * Checks if the endpoint can handle the request.
   * Typically the query path is checked.
   * 
   * @param request
   *   the incoming http request.
   * @return
   *   true in case this endpoint can process the request otherwise false.
   */
  boolean canHandle(HttpRequest request);

  /**
   * Handles and processes the incoming request.
   * 
   * @param request
   *   the incoming request
   * @param response
   *   the outgoing response
   * @throws Exception
   *   thrown in case the request could not be processed.
   */
  void handle(HttpRequest request, HttpResponse response) throws Exception;
}
