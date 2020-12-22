/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.log;

/**
 * Provides an interface which notifies upon new log messages.
 */
public interface LogListener {
  /**
   * Called whenever a new message was logged
   * @param message
   *   the message to be logged
   */
  void onLogMessage(String message);
}
