/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.exceptions;

/**
 * Signals that the test was aborted or failed because of an exception.
 */
public class SieveTestException extends Exception {

  private static final long serialVersionUID = -5545711196023514683L;

  /**
   * Creates a new instance
   * @param string
   *   the exception message as string.
   */
  public SieveTestException(final String string) {
    super(string);
  }

}
