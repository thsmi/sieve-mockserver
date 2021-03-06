/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.config;

public class ConfigurationException extends Exception {

  private static final long serialVersionUID = 3394506758273129222L;

  /**
   * Creates a new instance
   * @param message
   *   the error message describing the configuration error.
   */
  public ConfigurationException(final String message) {
    super(message);
  }
}
